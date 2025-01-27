package com.carpBread.shareEatIt.domain.sharingPost.service;


import com.carpBread.shareEatIt.domain.member.dto.response.MemberAsWriterSimpleDtoComponent;
import com.carpBread.shareEatIt.domain.member.dto.response.LocationResponseDtoComponent;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.participation.entity.GratitudeSticker;
import com.carpBread.shareEatIt.domain.participation.entity.GratitudeType;
import com.carpBread.shareEatIt.domain.participation.entity.Participation;
import com.carpBread.shareEatIt.domain.participation.entity.ParticipationStatus;
import com.carpBread.shareEatIt.domain.participation.repository.GratitudeStickerRepository;
import com.carpBread.shareEatIt.domain.sharingPost.dto.SharingPostListRequestDto;
import com.carpBread.shareEatIt.domain.sharingPost.dto.SharingPostListResponseDto;
import com.carpBread.shareEatIt.domain.sharingPost.dto.SharingPostResponseDto;
import com.carpBread.shareEatIt.domain.sharingPost.dto.SharingPostSimpleResponseComponent;
import com.carpBread.shareEatIt.domain.sharingPost.entity.PostImgUrl;
import com.carpBread.shareEatIt.domain.sharingPost.entity.PostType;
import com.carpBread.shareEatIt.domain.sharingPost.entity.SharingPost;
import com.carpBread.shareEatIt.domain.sharingPost.repository.PostImgUrlRepository;
import com.carpBread.shareEatIt.domain.sharingPost.repository.SharingPostQuerydslRepository;
import com.carpBread.shareEatIt.domain.sharingPost.repository.SharingPostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/* 나눔글 조회 관련 service */
@Service
@RequiredArgsConstructor
public class SharingPostReadService {

    // repository
    private final SharingPostRepository sharingPostRepository;
    private final SharingPostQuerydslRepository sharingPostQuerydslRepository;
    private final PostImgUrlRepository postImgUrlRepository;
    private final GratitudeStickerRepository gratitudeStickerRepository;


    // 위치 기반 주변 post 반경 (10km 설정)
    private final double radius = 100000;

    /* provider 타입에 따른 post 리스트 조회 */
    @Transactional
    public SharingPostListResponseDto findPostListByProviderType(SharingPostListRequestDto dto) {
        // 점검 : MySQL 8.4 Reference Manual 에 정의된 메뉴얼에 따라, latitude(위도)는 [-90.0, 90.0] / longitude(경도)는 [-180.0, 180.0] 범위로 지정
        if ((dto.getLatitude()>90.0 || dto.getLatitude()<-90.0)
                || (dto.getLongitude()>180.0 || dto.getLongitude()<-180.0)){
            /* throw new CustomException(CustomExceptionStatus.VALUE_OUT_OF_RANGE,"입력한 위도 혹은 경도 값이 범위를 초과하거나 미만입니다. 범위를 재점검해주십시오.","/sharing/list")*/;
        }

        // 나눔글 리스트 반환
        List<SharingPost> postList = findPostList(dto);

        // 나눔글 객체 미리보기 list component 생성
        List<SharingPostSimpleResponseComponent> componentList = changeSharingPostEntityListToComponentList(postList);

        return SharingPostListResponseDto.builder()
                .provider(dto.getPostType())
                .postList(componentList)
                .build();
    }


    /* postId로 나눔글 조회 */
    @Transactional
    public SharingPostResponseDto findSharingPostByID(Member member, Long id) {
        // 나눔글 조회
        SharingPost findPost = sharingPostRepository.findById(id)
                .orElseThrow(() -> null /* new CustomException(CustomExceptionStatus.NOT_FOUND_POST,
                        "해당 id에 대응하는 SHARING POST가 존재하지 않습니다.",
                        "/sharing/" + id)*/);

        // subject 지정
        String subject = determineSubject(findPost, member);

        // response dto component 생성
        MemberAsWriterSimpleDtoComponent writer = getSimpleWriterComponent(findPost.getWriter());
        LocationResponseDtoComponent location = getLocationComponent(findPost);
        GratitudeType gratitudeSticker = getGratitudeSticker(findPost);

        // 나눔글 이미지 리스트 component
        List<String> imgUrlList = new ArrayList<>();
        for (PostImgUrl img : getPostImgUrlList(findPost)) {
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


    /* 나눔글 작성자 simple writer component 생성 */
    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    private MemberAsWriterSimpleDtoComponent getSimpleWriterComponent(Member writer){
        return new MemberAsWriterSimpleDtoComponent(
                writer.getId(),
                writer.getProfileImgUrl(),
                writer.getNickname(),
                sharingPostRepository.countByWriter(writer)
        );
    }

    /* 나눔글의 평가 스티커 조회 */
    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    private GratitudeType getGratitudeSticker(SharingPost post){
        Boolean exists = gratitudeStickerRepository.existsByPost(post);
        if (exists){
            GratitudeSticker gratitudeSticker = gratitudeStickerRepository.findByPost(post)
                    .orElseThrow(() -> null/*new CustomException(CustomExceptionStatus.NOT_FOUND_POST, "GRATITUDE STICKER 객체를 통한 POST 객체를 조회할 수 없는 서버 내부 문제가 발생하였습니다.", "/sharing" + post.getId())*/);
            return gratitudeSticker.getGratitudeType();
        }
        else
            return null;
    }

    /* SharingPostListRequestDto로 나눔글 리스트 조회 */
    private List<SharingPost> findPostList(SharingPostListRequestDto dto){

        List<SharingPost> postList = new ArrayList<>();
        // post type이 전체일 경우
        if (dto.getPostType().equals("ALL")){
            postList = sharingPostQuerydslRepository.findSharingPostsWithinRadius(
                    dto.getLatitude(), dto.getLongitude(), radius
            );
        }
        // store 혹은 individual일 경우
        else if(dto.getPostType().equals(PostType.STORE.name()) ||
                dto.getPostType().equals(PostType.INDIVIDUAL.name())){
            postList = sharingPostQuerydslRepository.findSharingPostsByPostTypeWithinRadius(
                    dto.getLatitude(), dto.getLongitude(),
                    radius, PostType.toEnumType(dto.getPostType())
            );
        }else{
            /* throw new CustomException(CustomExceptionStatus.INVALID_ENUM_VALUE, "잘못된 SHARING POST TYPE ENUM 값 입니다","/sharing/list")*/;
        }

        return postList;

    }


    /* 나눔글 미리보기 객체 component 리스트 생성 */
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


    /* 만기 기한 dday 계산 */
    private int calculateDDay(LocalDateTime endAt){
        LocalDateTime now = LocalDateTime.now();
        return (int) ChronoUnit.DAYS.between(endAt,now);
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

    /* 나눔글 생성 기간 구하기(ex. 1분 전, 1시간 전) */
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

    /* 썸네일 이미지 url 추출 */
    private String findFirstImgUrl(SharingPost post){

        for (PostImgUrl imgUrl : getPostImgUrlList(post)){
            if (imgUrl.getImgOrder()==1){
                return imgUrl.getUrl();
            }
        }
        /* throw new CustomException(CustomExceptionStatus.NOT_FOUND_POST_IMAGE, "현재 POST에 해당하는 IMAGE를 찾을 수 없습니다", "/sharing")*/;
        return "";
    }

    /* 나눔글 만남 위치 locationComponent 생성 */
    private LocationResponseDtoComponent getLocationComponent(SharingPost post){
        return new LocationResponseDtoComponent(
                post.getAddressSt(),
                post.getAddressDetail(),
                post.getLocationPoint().getY(),
                post.getLocationPoint().getX()
        );
    }

    /* 나눔글 이미지 리스트 조회 */
    @Transactional(value = Transactional.TxType.REQUIRES_NEW)
    public List<PostImgUrl> getPostImgUrlList(SharingPost post){
        return postImgUrlRepository.findByPost(post);
    }

}
