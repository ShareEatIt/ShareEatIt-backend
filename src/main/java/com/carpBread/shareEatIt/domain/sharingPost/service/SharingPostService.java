package com.carpBread.shareEatIt.domain.sharingPost.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.carpBread.shareEatIt.domain.member.dto.LocationResponseDtoComponent;
import com.carpBread.shareEatIt.domain.member.dto.MemberAsWriterSimpleDtoComponent;
import com.carpBread.shareEatIt.domain.member.entity.Keywords;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.entity.Provider;
import com.carpBread.shareEatIt.domain.member.repository.KeywordsRepository;
import com.carpBread.shareEatIt.domain.member.repository.MemberRepository;
import com.carpBread.shareEatIt.domain.notice.dto.NoticeCreateDto;
import com.carpBread.shareEatIt.domain.notice.dto.NoticeRelatedObjectResponseComponent;
import com.carpBread.shareEatIt.domain.notice.entity.Notice;
import com.carpBread.shareEatIt.domain.notice.entity.NoticeType;
import com.carpBread.shareEatIt.domain.notice.repository.NoticeRepository;
import com.carpBread.shareEatIt.domain.notice.service.NoticeService;
import com.carpBread.shareEatIt.domain.participation.entity.GratitudeSticker;
import com.carpBread.shareEatIt.domain.participation.entity.GratitudeType;
import com.carpBread.shareEatIt.domain.participation.entity.Participation;
import com.carpBread.shareEatIt.domain.participation.entity.ParticipationStatus;
import com.carpBread.shareEatIt.domain.participation.repository.GratitudeStickerRepository;
import com.carpBread.shareEatIt.domain.sharingPost.dto.*;
import com.carpBread.shareEatIt.domain.sharingPost.dto.map.MapListResponseDto;
import com.carpBread.shareEatIt.domain.sharingPost.dto.map.MapRequestDto;
import com.carpBread.shareEatIt.domain.sharingPost.dto.map.MapResponseComponent;
import com.carpBread.shareEatIt.domain.sharingPost.entity.*;
import com.carpBread.shareEatIt.domain.sharingPost.repository.PostImgUrlRepository;
import com.carpBread.shareEatIt.domain.sharingPost.repository.SharingPostRepository;
import com.carpBread.shareEatIt.global.exception.AppException;
import com.carpBread.shareEatIt.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SharingPostService {
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    // 위치 기반 반경 (10km 설정)
    private final double radius = 10000;
    private final double mapRadius = 1000;

    private final GeometryFactory geometryFactory = new GeometryFactory();

    private final SharingPostRepository sharingPostRepository;
    private final MemberRepository memberRepository;
    private final KeywordsRepository keywordsRepository;
    private final NoticeRepository noticeRepository;
    private final PostImgUrlRepository postImgUrlRepository;
    private final GratitudeStickerRepository gratitudeStickerRepository;

    private final AmazonS3 s3Client;

    @Transactional
    public SharingPostCreateResponseDto createSharingPost(List<MultipartFile> imgList, SharingPostRequestDto dto, Member member){

        List<String> imgUrlList = uploadPostImgToS3Bucket(imgList);

        // post 저장

        // STORE로 설정할 경우 사용자가 STORE PROVIDER인지 점검
        if (dto.getPostType().equals("STORE") && member.getProvider()== Provider.INDIVIDUAL){
            throw new AppException(ErrorCode.INVALID_PROVIDER_WITH_POSTTYPE_STORE,"회원의 PROVIDER가 INDIVIDUAL일 경우 SharingPost를 STORE TYPE으로 설정하여 게시할 수 없습니다","/sharing");
        }

        Point point = geometryFactory.createPoint(new Coordinate(dto.getLongitude(), dto.getLatitude()));
        point.setSRID(4326);

        SharingPost newPost = SharingPost.builder()
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

        SharingPost savedPost = sharingPostRepository.save(newPost);

        isSendNotification(savedPost);

        // 이미지 저장
        for (int i=0; i<imgUrlList.size(); i++){
            PostImgUrl newImgEntity = PostImgUrl.builder()
                    .url(imgUrlList.get(i))
                    .imgOrder(i + 1)
                    .post(savedPost)
                    .build();

            postImgUrlRepository.save(newImgEntity);

        }

        // response dto 만들기
        MemberAsWriterSimpleDtoComponent writer = getSimpleWriterComponent(member);

        LocationResponseDtoComponent location = LocationResponseDtoComponent.builder()
                .addressSt(savedPost.getAddressSt())
                .addressDetail(savedPost.getAddressDetail())
                .latitude(savedPost.getLocationPoint().getY())
                .longitude(savedPost.getLocationPoint().getX())
                .build();
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
                throw new AppException(ErrorCode.AWS_S3_IMG_UPLOAD_CONNECTION_ERROR, "sharing post create - POST error","/sharing");
            }

            imgUrlList.add(s3Client.getUrl(bucketName,key).toString());

        }

        return imgUrlList;

    }

    @Transactional
    public SharingPostListResponseDto findPostListByProviderType(Member member, SharingPostListRequestDto dto) {

        List<SharingPost> postList = new ArrayList<>();


        if (dto.getPostType().equals("ALL")){
            postList = sharingPostRepository.findSharingPostsWithinRadius(dto.getLatitude(), dto.getLongitude(), radius);

        }else{
            postList = sharingPostRepository.findSharingPostsByPostTypeWithinRadius(dto.getLatitude(), dto.getLongitude(), radius, dto.getPostType());

        }

        List<SharingPostSimpleResponseComponent> componentList = changeSharingPostEntityListToComponentList(postList);

        return SharingPostListResponseDto.builder()
                .provider(dto.getPostType())
                .postList(componentList)
                .build();

    }

    @Transactional
    public SharingPostResponseDto findSharingPostByID(Member member, Long id) {
        SharingPost findPost = sharingPostRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_POST, "해당 id에 대응하는 SHARING POST가 존재하지 않습니다.", "/sharing/" + id));

        String subject = determineSubject(findPost, member);
        MemberAsWriterSimpleDtoComponent writer = getSimpleWriterComponent(member);
        LocationResponseDtoComponent location = getLocationComponent(findPost);
        GratitudeType gratitudeSticker = getGratitudeSticker(findPost);

        List<String> imgUrlList = new ArrayList<>();
        for (PostImgUrl img : findPost.getPostImgUrlList()){
            imgUrlList.add(img.getUrl());
        }

        return SharingPostResponseDto.builder()
                .id(findPost.getId())
                .title(findPost.getTitle())
                .imgList(imgUrlList)
                .category(findPost.getCategory().name())
                .isFinished(findPost.getIsFinished())
                .foodName(findPost.getFoodName())
                .expDate(findPost.getExpDate())
                .purchaseDate(findPost.getPurchaseDate())
                .location(location)
                .endAt(findPost.getEndAt())
                .createdAt(findPost.getCreatedAt())
                .modifiedAt(findPost.getModifiedAt())
                .writer(writer)
                .postType(findPost.getPostType().name())
                .description(findPost.getDescription())
                .status(findPost.getStatus().name())
                .subject(subject)
                .gratitudeSticker(gratitudeSticker!=null ? gratitudeSticker.name(): null)
                .build();


    }


    @Transactional
    public SharingPostResponseDto updateSharingPost(Member member, Long id, List<MultipartFile> imgList, SharingPostUpdateRequestDto dto) {
        SharingPost targetPost = sharingPostRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_POST, "해당 id에 대응하는 SHARING POST가 존재하지 않습니다.", "/sharing/" + id));

        if (targetPost.getWriter().getId() != member.getId())
            throw new AppException(ErrorCode.UNAUTHORIZED_MEMBER_TO_UPDATE_POST, "작성자가 아니므로 해당 POST에 대한 내용 수정이 불가합니다.", "/sharing/" + id);

        Point point = geometryFactory.createPoint(new Coordinate(dto.getLongitude(), dto.getLatitude()));
        point.setSRID(4326);

        targetPost.updatePost(dto, point);

        SharingPost updatedPost = sharingPostRepository.save(targetPost);

        // 이미지 리스트 확인
        boolean result = updatePostImgList(dto.getImgUrlList(), getPostImgUrlList(updatedPost), targetPost);
        List<PostImgUrl> updatedUrlList = getPostImgUrlList(updatedPost);

        System.out.println("현재 리스트 수: "+updatedUrlList.size());

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
                    throw new AppException(ErrorCode.AWS_S3_IMG_UPLOAD_CONNECTION_ERROR, "sharing post create - POST error", "/sharing");
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


        LocationResponseDtoComponent location = getLocationComponent(updatedPost);
        MemberAsWriterSimpleDtoComponent writer = getSimpleWriterComponent(member);
        GratitudeType gratitudeSticker = getGratitudeSticker(updatedPost);
        updatedUrlList = getPostImgUrlList(updatedPost);
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
                .gratitudeSticker(gratitudeSticker!=null ? gratitudeSticker.name(): null)
                .build();

    }

    @Transactional
    public void deleteSharingPost(Member member, Long id) {
        SharingPost targetPost = sharingPostRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_POST, "해당 id에 대응하는 SHARING POST가 존재하지 않습니다.", "/sharing/" + id));

        if (targetPost.getWriter().getId() != member.getId())
            throw new AppException(ErrorCode.UNAUTHORIZED_MEMBER_TO_DELETE_POST, "작성자가 아니므로 해당 POST에 대한 삭제가 불가합니다.", "/sharing/" + id);

        sharingPostRepository.delete(targetPost);

    }


    @Transactional
    public MapListResponseDto getMapList(Member member, MapRequestDto dto) {
        List<SharingPost> sharingPostsWithinRadius = sharingPostRepository.findSharingPostsWithinRadius(dto.getLatitude(), dto.getLongitude(), mapRadius);

        List<MapResponseComponent> componentList = new ArrayList<>();
        for (SharingPost post : sharingPostsWithinRadius){
            LocationResponseDtoComponent location = LocationResponseDtoComponent.builder()
                    .latitude(post.getLocationPoint().getY())
                    .longitude(post.getLocationPoint().getX())
                    .addressDetail(post.getAddressDetail())
                    .addressSt(post.getAddressSt())
                    .build();


            MapResponseComponent component = MapResponseComponent.builder()
                    .kakaoLocationCode(post.getKakaoLocationCode())
                    .id(post.getId())
                    .category(post.getCategory().name())
                    .location(location)
                    .build();

            componentList.add(component);


        }

        return MapListResponseDto.builder()
                .mapList(componentList)
                .build();

    }


    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    public List<PostImgUrl> getPostImgUrlList(SharingPost post){
        return postImgUrlRepository.findByPost(post);
    }


    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    public boolean updatePostImgList(List<String> currentUrlList, List<PostImgUrl> preUrlList, SharingPost post){
        if (currentUrlList.size()==preUrlList.size())
            return false;
        if (currentUrlList.size()==0) {
            postImgUrlRepository.deleteAllByPost(post);
            return true;
        }

        List<PostImgUrl> listToDelete = preUrlList.stream()
                .filter(preImgUrl -> !currentUrlList.contains(preImgUrl.getUrl()))
                .collect(Collectors.toList());

        System.out.println("삭제할 객체의 수 : "+listToDelete.size());


        for (PostImgUrl deleteUrl: listToDelete){


            System.out.println("==========");
            postImgUrlRepository.deleteById(deleteUrl.getId());
            System.out.println("============");

            // objectkey 추출
            String objectKey = URI.create(deleteUrl.getUrl())
                    .getPath().substring(1);
            s3Client.deleteObject(bucketName, objectKey);

        }

        return true;

    }

    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    private MemberAsWriterSimpleDtoComponent getSimpleWriterComponent(Member writer){
        return MemberAsWriterSimpleDtoComponent.builder()
                .id(writer.getId())
                .img(writer.getProfileImgUrl())
                .nickname(writer.getNickname())
                .sharingTotal(sharingPostRepository.countByWriter(writer))
                .build();

    }

    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    private GratitudeType getGratitudeSticker(SharingPost post){
        Boolean exists = gratitudeStickerRepository.existsByPost(post);
        if (exists){
            GratitudeSticker gratitudeSticker = gratitudeStickerRepository.findByPost(post)
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_POST, "GRATITUDE STICKER 객체를 통한 POST 객체를 조회할 수 없는 서버 내부 문제가 발생하였습니다.", "/sharing" + post.getId()));

            return gratitudeSticker.getGratitudeType();
        }
        else
            return null;

    }

    private LocationResponseDtoComponent getLocationComponent(SharingPost post){
        return LocationResponseDtoComponent.builder()
                .addressSt(post.getAddressSt())
                .addressDetail(post.getAddressDetail())
                .latitude(post.getLocationPoint().getY())
                .longitude(post.getLocationPoint().getX())
                .build();
    }

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


    private List<SharingPostSimpleResponseComponent> changeSharingPostEntityListToComponentList(List<SharingPost> entityList){
        List<SharingPostSimpleResponseComponent> componentList = new ArrayList<>();

        for (SharingPost entity : entityList){
            int dDay = calculateDDay(entity.getEndAt());
            String ago = calculateAgo(entity.getCreatedAt());
            String firstImgUrl = findFirstImgUrl(entity);

            SharingPostSimpleResponseComponent component = SharingPostSimpleResponseComponent.builder()
                    .id(entity.getId())
                    .createdAt(entity.getCreatedAt())
                    .title(entity.getTitle())
                    .endAt(entity.getEndAt())
                    .nickname(entity.getWriter().getNickname())
                    .category(entity.getCategory().name())
                    .dDay(dDay)
                    .ago(ago)
                    .img(firstImgUrl)
                    .build();

            componentList.add(component);
        }

        return componentList;
    }

    private int calculateDDay(LocalDateTime endAt){
        LocalDateTime now = LocalDateTime.now();

        return (int) ChronoUnit.DAYS.between(endAt,now);
    }

    private String calculateAgo(LocalDateTime createdAt){
        LocalDateTime now = LocalDateTime.now();

        long seconds = ChronoUnit.SECONDS.between(createdAt,now);
        long minutes = ChronoUnit.MINUTES.between(createdAt, now);
        long hours = ChronoUnit.HOURS.between(createdAt, now);
        long days = ChronoUnit.DAYS.between(createdAt, now);
        long weeks = days/7;

        if(seconds<60){
            return seconds+"초 전";
        } else if (minutes<60) {
            return minutes+"분 전";
        } else if (hours < 24) {
            return hours+"시간 전";
        } else if (days<7) {
            return days+"일 전";
        } else{
            return weeks+"주 전";
        }

    }

    private String findFirstImgUrl(SharingPost post){

//        PostImgUrl firstImgUrl = postImgUrlRepository.findByImgOrderAndPost(1, post)
//                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_POST_IMAGE, "현재 POST에 해당하는 IMAGE를 찾을 수 없습니다", "/sharing"));
//
//        return firstImgUrl.getUrl();

        for (PostImgUrl imgUrl : getPostImgUrlList(post)){
            if (imgUrl.getImgOrder()==1){
                return imgUrl.getUrl();
            }
        }
        throw new AppException(ErrorCode.NOT_FOUND_POST_IMAGE, "현재 POST에 해당하는 IMAGE를 찾을 수 없습니다", "/sharing");

    }

    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    private void isSendNotification(SharingPost post){
        List<Member> memberList = memberRepository.findMemberWithRadius(post.getLocationPoint().getY(), post.getLocationPoint().getX(), mapRadius);

        String foodName = post.getFoodName();

        for (Member member : memberList){
            List<Keywords> keywordsList = member.getKeywordsList();

            for(Keywords keywords : keywordsList){
                String keyword = keywords.getKeyword();
                if ((keyword.length()>=foodName.length() && foodName.contains(keyword) )
                    || (keyword.length()< foodName.length() && keyword.contains(foodName))){
                    String title="새로운 나눔글이 등록되었어요!✨";
                    String message = member.getNickname() + "님을 위한 " + keyword + "과 관련된 새로운 나눔글이 등록되었어요!✨ \n 관심 키워드로 등록한 나눔글을 확인해보세요❤️";

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
                            .noticeType(NoticeType.KEYWORD)
                            .noticeObject(noticeObject)
                            .createdAt(savedNotice.getCreatedAt())
                            .build();

                    NoticeService.sendNotification(member, noticeDto);

                }
            }
        }

    }



}
