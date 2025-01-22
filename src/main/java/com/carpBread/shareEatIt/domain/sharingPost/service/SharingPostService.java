package com.carpBread.shareEatIt.domain.sharingPost.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.carpBread.shareEatIt.domain.member.dto.response.LocationResponseDtoComponent;
import com.carpBread.shareEatIt.domain.member.dto.MemberAsWriterSimpleDtoComponent;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.entity.Provider;
import com.carpBread.shareEatIt.domain.participation.entity.GratitudeSticker;
import com.carpBread.shareEatIt.domain.participation.entity.GratitudeType;
import com.carpBread.shareEatIt.domain.participation.entity.Participation;
import com.carpBread.shareEatIt.domain.participation.entity.ParticipationStatus;
import com.carpBread.shareEatIt.domain.participation.repository.GratitudeStickerRepository;
import com.carpBread.shareEatIt.domain.participation.repository.ParticipationRepository;
import com.carpBread.shareEatIt.domain.sharingPost.dto.*;
import com.carpBread.shareEatIt.domain.sharingPost.entity.*;
import com.carpBread.shareEatIt.domain.sharingPost.repository.PostImgUrlRepository;
import com.carpBread.shareEatIt.domain.sharingPost.repository.SharingPostRepository;
import com.carpBread.shareEatIt.global.exception.CustomException;
import com.carpBread.shareEatIt.global.exception.CustomExceptionStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


/* 나눔글 관련 service  */
@Service
@RequiredArgsConstructor
public class SharingPostService {

    // 위치 point
    private final GeometryFactory geometryFactory;

    // repository
    private final SharingPostRepository sharingPostRepository;
    private final ParticipationRepository participationRepository;
    private final PostImgUrlRepository postImgUrlRepository;
    private final GratitudeStickerRepository gratitudeStickerRepository;

    // aws s3 client
    private final AmazonS3 s3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    /* 나눔글 내용 수정 */
    @Transactional
    public SharingPostResponseDto updateSharingPost(Member member, Long id, List<MultipartFile> imgList, SharingPostUpdateRequestDto dto) {
        SharingPost targetPost = sharingPostRepository.findById(id)
                .orElseThrow(() -> null /*new CustomException(CustomExceptionStatus.NOT_FOUND_POST, "해당 id에 대응하는 SHARING POST가 존재하지 않습니다.", "/sharing/" + id)*/);

        // 점검 1 : 작성자 본인인지 확인 -> 작성자가 아닐 경우 삭제 불가
        if (targetPost.getWriter().getId() != member.getId()){}
            /* throw new CustomException(CustomExceptionStatus.UNAUTHORIZED_MEMBER_TO_UPDATE_POST, "작성자가 아니므로 해당 POST에 대한 내용 수정이 불가합니다.", "/sharing/" + id)*/;

        // 점검 2 : 참여가 진행중이거나 완료된 sharing post 일 경우 내용 수정 불가
        List<Participation> participationList = participationRepository.findByPostIdAndStatus(targetPost.getId());
        if (participationList.size()!=0 || targetPost.getStatus()==PostStatus.COMPLETED) {
            /* throw new CustomException(CustomExceptionStatus.UNAUTHORIZED_UPDATE_POST, "참여가 완료된 나눔이므로 POST에 대한 내용 수정이 불가합니다", "/sharing" + id)*/;
        }

        // 점검 3 : 변경하고자 하는 posttype이 store인 경우 member의 Provider가 Store인지 점검
        if (dto.getPostType().equals(PostType.STORE.name()) && member.getProvider().name().equals(Provider.INDIVIDUAL.name())){
            /* throw new CustomException(CustomExceptionStatus.INVALID_PROVIDER_WITH_POSTTYPE_STORE,
                    "회원의 PROVIDER 가 `개인`으로 설정되어있어 나눔글을 STORE로 변경할 수 없습니다",
                    "/sharing")*/;
        }

        // 점검 4 : MySQL 8.4 Reference Manual 에 정의된 메뉴얼에 따라, latitude(위도)는 [-90.0, 90.0] / longitude(경도)는 [-180.0, 180.0] 범위로 지정
        if ((dto.getLatitude()>90.0 || dto.getLatitude()<-90.0)
                || (dto.getLongitude()>180.0 || dto.getLongitude()<-180.0)){
            /* throw new CustomException(CustomExceptionStatus.VALUE_OUT_OF_RANGE,"입력한 위도 혹은 경도 값이 범위를 초과하거나 미만입니다. 범위를 재점검해주십시오.","/members")*/;
        }

        // point 객체 생성
        Point point = createPoint(dto.getLongitude(), dto.getLatitude());

        // 업데이트하고자 하는 post 업데이트
        targetPost.updatePost(dto, point);

        // 업데이트한 post 저장
        SharingPost updatedPost = sharingPostRepository.save(targetPost);

        // 이미지 리스트 확인
        // 이전 이미지 삭제
        updatePastPostImgList(dto.getImgUrlList(), getPostImgUrlList(updatedPost), targetPost);
        saveNewSharingPostImages(updatedPost, imgList);

        // request dto component 생성
        LocationResponseDtoComponent location = getLocationComponent(updatedPost);
        MemberAsWriterSimpleDtoComponent writer = getSimpleWriterComponent(member);
        GratitudeType gratitudeSticker = getGratitudeSticker(updatedPost);
        List<PostImgUrl> updatedUrlList = getPostImgUrlList(updatedPost);
        String subject = determineSubject(updatedPost, member);

        List<String> imgUrlList = new ArrayList<>();
        for (PostImgUrl imgUrl : updatedUrlList) {
            imgUrlList.add(imgUrl.getUrl());
        }

        return SharingPostResponseDto.builder()
                .id(updatedPost.getId())
                .title(updatedPost.getTitle())
                .imgList(imgUrlList)
                .category(updatedPost.getCategory().name())
                .isFinished(updatedPost.getIsFinished())
                .foodName(updatedPost.getFoodName())
                .expDate(updatedPost.getExpDate())
                .purchaseDate(updatedPost.getPurchaseDate())
                .location(location)
                .endAt(updatedPost.getEndAt())
                .createdAt(updatedPost.getCreatedAt())
                .modifiedAt(updatedPost.getModifiedAt())
                .writer(writer)
                .postType(updatedPost.getPostType().name())
                .description(updatedPost.getDescription())
                .status(updatedPost.getStatus().name())
                .subject(subject)
                .gratitudeSticker(gratitudeSticker!=null ? gratitudeSticker.name(): null)
                .build();

    }

    /* 나눔글 삭제 */
    @Transactional
    public void deleteSharingPost(Member member, Long id) {
        // 나눔글 조회
        SharingPost targetPost = sharingPostRepository.findById(id)
                .orElseThrow(() -> null /*new CustomException(CustomExceptionStatus.NOT_FOUND_POST, "해당 id에 대응하는 SHARING POST가 존재하지 않습니다.", "/sharing/" + id)*/);

        // 나눔글 작제 권한 여부 조회
        if (targetPost.getWriter().getId() != member.getId())
            /* throw new CustomException(CustomExceptionStatus.UNAUTHORIZED_MEMBER_TO_DELETE_POST, "작성자가 아니므로 해당 POST에 대한 삭제가 불가합니다.", "/sharing/" + id)*/;

        // 나눔글 삭제
        sharingPostRepository.delete(targetPost);

    }

    /* 나눔글 이미지 리스트 조회 */
    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    public List<PostImgUrl> getPostImgUrlList(SharingPost post){
        return postImgUrlRepository.findByPost(post);
    }




    /**********************************************************************/




    /* 나눔글의 이전 이미지 목록 리스트 삭제 및 업데이트 */
    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    private void updatePastPostImgList(List<String> currentUrlList, List<PostImgUrl> preUrlList, SharingPost post){

        // 삭제하려는 이미지가 없는 경우 false 리턴
        if (currentUrlList.size()==preUrlList.size())
            return;

        // 이미지 db에서 삭제 및 s3 버킷에 삭제 요청
        List<PostImgUrl> listToDelete = preUrlList.stream()
                .filter(preImgUrl -> !currentUrlList.contains(preImgUrl.getUrl()))
                .collect(Collectors.toList());

        for (PostImgUrl deleteUrl: listToDelete){

            postImgUrlRepository.deleteById(deleteUrl.getId());

            // objectkey 추출
            String objectKey = URI.create(deleteUrl.getUrl())
                    .getPath().substring(1);
            s3Client.deleteObject(bucketName, objectKey);
        }
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

    /* 나눔글의 평가 스티커 조회 */
    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    private GratitudeType getGratitudeSticker(SharingPost post){
        Boolean exists = gratitudeStickerRepository.existsByPost(post);
        if (exists){
            GratitudeSticker gratitudeSticker = gratitudeStickerRepository.findByPost(post)
                    .orElseThrow(() -> null /* new CustomException(CustomExceptionStatus.NOT_FOUND_POST, "GRATITUDE STICKER 객체를 통한 POST 객체를 조회할 수 없는 서버 내부 문제가 발생하였습니다.", "/sharing" + post.getId())*/);
            return gratitudeSticker.getGratitudeType();
        }
        else
            return null;
    }

    /* 나눔글 만남 위치 locationComponent 생성 */
    private LocationResponseDtoComponent getLocationComponent(SharingPost post){
        return LocationResponseDtoComponent.builder()
                .addressSt(post.getAddressSt())
                .addressDetail(post.getAddressDetail())
                .latitude(post.getLocationPoint().getY())
                .longitude(post.getLocationPoint().getX())
                .build();
    }

    /* 나눔글 조회 사용자 구분 - 작성자/참여자/제 3자 */
    // subject : WRITER, PARTICIPANT, VIEWER
    private String determineSubject(SharingPost post, Member member){
        Member writer = post.getWriter();

        if(member.getId() == writer.getId()){
            return "WRITER";
        }

        List<Participation> participationList = post.getParticipationList();
        for (Participation p : participationList){
            if (p.getReceiver().getId() == member.getId()){
                if (p.getStatus() == ParticipationStatus.COMPLETED || p.getStatus()==ParticipationStatus.MATCHED){
                    return "PARTICIPANT";
                }
            }
        }
        return "VIEWER";
    }

    /* point 생성 */
    private Point createPoint(Double longitude, Double latitude){
        return geometryFactory.createPoint(new Coordinate(longitude, latitude));
    }

    /* 나눔글 업데이트 시 새로운 이미지 파일 s3 업로드 및 파일 순서 변경 */
    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    private void saveNewSharingPostImages(SharingPost updatedPost, List<MultipartFile> imgList){
        List<PostImgUrl> updatedUrlList = getPostImgUrlList(updatedPost);

        int idx = 1;
        for (PostImgUrl imgUrl : updatedUrlList) {
            imgUrl.updateOrder(idx);
            postImgUrlRepository.save(imgUrl);
            idx += 1;
        }

        if (imgList!=null) {
            for (MultipartFile img : imgList) {
                String key = "images/" + UUID.randomUUID() + "_" + img.getOriginalFilename();

                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(img.getSize());
                metadata.setContentType(img.getContentType());

                try (InputStream inputStream = img.getInputStream()) {
                    s3Client.putObject(bucketName, key, inputStream, metadata);
                } catch (IOException e) {
                    /* throw new CustomException(CustomExceptionStatus.AWS_S3_IMG_UPLOAD_CONNECTION_ERROR, "sharing post create - POST error", "/sharing")*/;
                }

                String newUrl = s3Client.getUrl(bucketName, key).toString();
                PostImgUrl newUrlEntity = PostImgUrl.builder()
                        .post(updatedPost)
                        .imgOrder(idx)
                        .url(newUrl)
                        .build();
                idx+=1;
                postImgUrlRepository.save(newUrlEntity);
            }
        }
    }

}
