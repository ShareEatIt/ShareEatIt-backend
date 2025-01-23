package com.carpBread.shareEatIt.domain.sharingPost.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.carpBread.shareEatIt.domain.member.dto.MemberAsWriterSimpleDtoComponent;
import com.carpBread.shareEatIt.domain.member.dto.response.LocationResponseDtoComponent;
import com.carpBread.shareEatIt.domain.member.entity.Keywords;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.entity.Provider;
import com.carpBread.shareEatIt.domain.member.repository.MemberQuerydslRepository;
import com.carpBread.shareEatIt.domain.notice.dto.NoticeCreateDto;
import com.carpBread.shareEatIt.domain.notice.dto.NoticeRelatedObjectResponseComponent;
import com.carpBread.shareEatIt.domain.notice.entity.Notice;
import com.carpBread.shareEatIt.domain.notice.entity.NoticeType;
import com.carpBread.shareEatIt.domain.notice.repository.NoticeRepository;
import com.carpBread.shareEatIt.domain.notice.service.SseService;
import com.carpBread.shareEatIt.domain.sharingPost.dto.SharingPostCreateResponseDto;
import com.carpBread.shareEatIt.domain.sharingPost.dto.SharingPostRequestDto;
import com.carpBread.shareEatIt.domain.sharingPost.entity.*;
import com.carpBread.shareEatIt.domain.sharingPost.repository.PostImgUrlRepository;
import com.carpBread.shareEatIt.domain.sharingPost.repository.SharingPostRepository;
import com.carpBread.shareEatIt.global.exception.CustomException;
import com.carpBread.shareEatIt.global.exception.CustomExceptionStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CreateSharingPostService {

    // 위치 기반 주변 post 반경 (10km 설정)
    private final double radius = 100000;

    // 위치 point
    private final GeometryFactory geometryFactory;

    // repository
    private final SharingPostRepository sharingPostRepository;
    private final MemberQuerydslRepository memberQuerydslRepository;
    private final NoticeRepository noticeRepository;
    private final PostImgUrlRepository postImgUrlRepository;

    // 알람
    private final SseService sseService;

    // aws s3 client
    private final AmazonS3 s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    /* 나눔글 생성 */
    @Transactional
    public SharingPostCreateResponseDto createSharingPost(List<MultipartFile> imgList, SharingPostRequestDto dto, Member member){

        // post 저장
        // STORE로 설정할 경우 사용자가 STORE PROVIDER인지 점검
        if (dto.getPostType().equals("STORE") && member.getProvider()== Provider.INDIVIDUAL){
            /* throw new CustomException(CustomExceptionStatus.INVALID_PROVIDER_WITH_POSTTYPE_STORE,"회원의 PROVIDER가 INDIVIDUAL일 경우 SharingPost를 STORE TYPE으로 설정하여 게시할 수 없습니다","/sharing")*/;
        }


        // 점검 : MySQL 8.4 Reference Manual 에 정의된 메뉴얼에 따라, latitude(위도)는 [-90.0, 90.0] / longitude(경도)는 [-180.0, 180.0] 범위로 지정
        if ((dto.getLatitude()>90.0 || dto.getLatitude()<-90.0)
                || (dto.getLongitude()>180.0 || dto.getLongitude()<-180.0)){
            /* throw new CustomException(CustomExceptionStatus.VALUE_OUT_OF_RANGE,"입력한 위도 혹은 경도 값이 범위를 초과하거나 미만입니다. 범위를 재점검해주십시오.","/members")*/;
        }

        // point 객체 생성
        Point point = geometryFactory.createPoint(new Coordinate(dto.getLongitude(), dto.getLatitude()));
        point.setSRID(4326);

        // 새로운 sharingPost 객체 생성
        SharingPost newPost = createNewSharingPost(dto,point,member);

        // sharingPost 객체 db에 저장
        SharingPost savedPost = sharingPostRepository.save(newPost);

        // 나눔글 업로드 시 알림 보내기
        isSendNotification(savedPost);

        // 이미지 저장 및 업로드
        List<String> imgUrlList = uploadPostImgToS3Bucket(imgList);
        saveImageList(imgUrlList,savedPost);

        // response dto 만들기
        // 글쓴이 dto
        MemberAsWriterSimpleDtoComponent writer = getSimpleWriterComponent(member);

        // 나눔글 위치 dto
        LocationResponseDtoComponent location = generateLocationResponseDto(savedPost);

        return SharingPostCreateResponseDto.builder()
                .id(savedPost.getId())
                .writer(writer)
                .title(savedPost.getTitle())
                .category(savedPost.getCategory().name())
                .isFinished(savedPost.getIsFinished())
                .foodName(savedPost.getFoodName())
                .status(savedPost.getStatus().name())
                .postType(savedPost.getPostType().name())
                .expDate(savedPost.getExpDate())
                .purchaseDate(savedPost.getPurchaseDate())
                .imgList(imgUrlList)
                .location(location)
                .kakaoLocationCode(savedPost.getKakaoLocationCode())
                .description(savedPost.getDescription())
                .endAt(savedPost.getEndAt())
                .createdAt(savedPost.getCreatedAt())
                .build();


    }

    /* keyword notice 보내기 */
    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    private void isSendNotification(SharingPost post){

        Double latitude = post.getLocationPoint().getY();
        Double longitude = post.getLocationPoint().getX();

        // 점검 : MySQL 8.4 Reference Manual 에 정의된 메뉴얼에 따라, latitude(위도)는 [-90.0, 90.0] / longitude(경도)는 [-180.0, 180.0] 범위로 지정
        if ((latitude>90.0 || latitude<-90.0)
                || (longitude>180.0 || longitude<-180.0)){
            /* throw new CustomException(CustomExceptionStatus.VALUE_OUT_OF_RANGE,"위도 혹은 경도 값이 범위를 초과하거나 미만입니다. 범위를 재점검해주십시오.","/sharing")*/;
        }

        List<Member> memberList = memberQuerydslRepository.findMemberWithRadius(latitude,longitude, radius);

        for (Member member : memberList){

            // isKeywordAvail가 false 또는 clients에 등록되지 않은 경우 경우 알람 보내지 않음
            if(!member.getIsKeywordAvail() || !sseService.isRegistered(member.getId()))
                continue;

            List<Keywords> keywordsList = member.getKeywordsList();


            for(Keywords keywords : keywordsList){
                String keyword = keywords.getKeyword();
                System.out.println(keyword);
                if (post.getCategory().name().equals(keyword)){
                    String title="새로운 나눔글이 등록되었어요!✨";
                    String message = member.getNickname() + "님을 위한 " + keyword + "과 관련된 새로운 나눔글이 등록되었어요!✨ \n관심 키워드로 등록한 나눔글을 확인해보세요❤️";

                    Notice newNotice = Notice.builder()
                            .title(title)
                            .message(message)
                            .type(NoticeType.KEYWORD)
                            .isRead(false)
                            .member(member)
                            .build();
                    Notice savedNotice = noticeRepository.save(newNotice);

                    NoticeRelatedObjectResponseComponent noticeObject = NoticeRelatedObjectResponseComponent.builder()
                            .id(post.getId())
                            .category(post.getCategory().name())
                            .build();


                    NoticeCreateDto noticeDto = NoticeCreateDto.builder()
                            .id(savedNotice.getId())
                            .title(title)
                            .message(message)
                            .noticeType(NoticeType.KEYWORD.name())
                            .noticeObject(noticeObject)
                            .createdAt(savedNotice.getCreatedAt())
                            .build();

                    sseService.sendNotification(member.getId(), noticeDto);

                }
            }
        }

    }

    /* 새로운 sharingpost 객체 생성 */
    private SharingPost createNewSharingPost(SharingPostRequestDto dto,
                                             Point point,
                                             Member member){

        return SharingPost.builder()
                .title(dto.getTitle())
                .category(PostCategory.toEnumType(dto.getCategory()))
                .isFinished(dto.getIsFinished())
                .foodName(dto.getFoodName())
                .expDate(dto.getExpDate())
                .endAt(dto.getEndAt())
                .purchaseDate(dto.getPurchaseDate())
                .addressSt(dto.getAddressSt())
                .addressDetail(dto.getAddressDetail())
                .kakaoLocationCode(dto.getKakaoLocationCode())
                .locationPoint(point)
                .description(dto.getDescription())
                .postType(PostType.toEnumType(dto.getPostType()))
                .status(PostStatus.AVAILABLE)
                .writer(member)
                .noticed(false)
                .build();
    }

    /* 나눔글 작성자 simple writer component 생성 */
    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    private MemberAsWriterSimpleDtoComponent getSimpleWriterComponent(Member writer){
        return MemberAsWriterSimpleDtoComponent.builder()
                .id(writer.getId())
                .img(writer.getProfileImgUrl())
                .nickname(writer.getNickname())
                .sharingTotal(sharingPostRepository.countByWriter(writer))
                .build();

    }

    /* 나눔글 위치 response component 생성 */
    private LocationResponseDtoComponent generateLocationResponseDto(SharingPost savedPost){
        return LocationResponseDtoComponent.builder()
                .addressSt(savedPost.getAddressSt())
                .addressDetail(savedPost.getAddressDetail())
                .latitude(savedPost.getLocationPoint().getY())
                .longitude(savedPost.getLocationPoint().getX())
                .build();
    }

    /* 나눔글 이미지 s3 버킷에 업로드 */
    @Transactional
    private List<String> uploadPostImgToS3Bucket(List<MultipartFile> imgList) {

        List<String> imgUrlList = new ArrayList<>();

        for (MultipartFile img : imgList){
            String key = "images/" + UUID.randomUUID() + "_" + img.getOriginalFilename();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(img.getSize());
            metadata.setContentType(img.getContentType());

            try (InputStream inputStream = img.getInputStream()){
                s3Client.putObject(bucketName,key,inputStream,metadata);
            }
            catch (IOException e){
                /* throw new CustomException(CustomExceptionStatus.AWS_S3_IMG_UPLOAD_CONNECTION_ERROR, "sharing post create - POST error","/sharing")*/;
            }

            imgUrlList.add(s3Client.getUrl(bucketName,key).toString());

        }

        return imgUrlList;

    }

    /* 이미지 리스트 저장 */
    private void saveImageList(List<String> imgUrlList, SharingPost savedPost){
        for (int i=0; i<imgUrlList.size(); i++){
            PostImgUrl newImgEntity = PostImgUrl.builder()
                    .url(imgUrlList.get(i))
                    .imgOrder(i + 1)
                    .post(savedPost)
                    .build();

            postImgUrlRepository.save(newImgEntity);

        }
    }
}
