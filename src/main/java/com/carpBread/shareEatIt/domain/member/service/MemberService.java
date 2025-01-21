package com.carpBread.shareEatIt.domain.member.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.carpBread.shareEatIt.domain.member.controller.SentryTestController;
import com.carpBread.shareEatIt.domain.member.dto.request.MemberProfileUpdateRequestDto;
import com.carpBread.shareEatIt.domain.member.dto.response.*;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.repository.MemberRepository;
import com.carpBread.shareEatIt.domain.notice.service.SseService;
import com.carpBread.shareEatIt.domain.participation.repository.GratitudeStickerRepository;
import com.carpBread.shareEatIt.domain.sharingPost.entity.PostCategory;
import com.carpBread.shareEatIt.domain.sharingPost.repository.SharingPostRepository;
import com.carpBread.shareEatIt.global.exception.AppException;
import com.carpBread.shareEatIt.global.exception.ErrorCode;
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j @Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final GratitudeStickerRepository gratitudeStickerRepository;
    private final SharingPostRepository sharingPostRepository;
    private final SseService sseService;
    private final AmazonS3 s3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String sentryTest(SentryTestController.SentrySampleDto dto){

        log.debug("MemberService.sentryTest");
        if (dto.getName().equals("manager")){
            throw new AppException(ErrorCode.UNAUTHORIZED_USER,"'manager' 이름은 사용할 수 없습니다.","/sentry");
        }
        return "success";
    }

    /* 회원 스티커 현황 조회 */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public MemberStickerResponseDto findStickers(Member member){

        List<Object[]> stickers = gratitudeStickerRepository.countByGratitudeTypeByGiver(member.getId());
        Map<String , Long > responseList=new HashMap<>();

        for (Object[] objects : stickers){
            String type = (String) objects[0];
            Long count=(Long) objects[1];

            responseList.put(type,count);
        }

        StickersResponseDto stickersDto = StickersResponseDto.builder()
                .smile1(responseList.get("SMILE1"))
                .smile2(responseList.get("SMILE2"))
                .smile3(responseList.get("SMILE3"))
                .smile4(responseList.get("SMILE4"))
                .smile5(responseList.get("SMILE5"))
                .build();

        return MemberStickerResponseDto.builder()
                .id(member.getId())
                .profileImg(member.getProfileImgUrl())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .stickers(stickersDto)
                .isKeywordAvail(member.getIsKeywordAvail())
                .isNoticeAvail(member.getIsNoticeAvail())
                .provider(member.getProvider().name())
                .build();

    }

    /* 사용자 정보 수정 */
    public MemberProfileResponseDto updateProfile(Member member, MultipartFile imgFile, MemberProfileUpdateRequestDto updateRequestDto) {

        // 1. 점검 : MySQL 8.4 Reference Manual 에 정의된 메뉴얼에 따라, latitude(위도)는 [-90.0, 90.0] / longitude(경도)는 [-180.0, 180.0] 범위로 지정
        if ((updateRequestDto.getLatitude()>90.0 || updateRequestDto.getLatitude()<-90.0)
                || (updateRequestDto.getLongitude()>180.0 || updateRequestDto.getLongitude()<-180.0)){
            throw new AppException(ErrorCode.VALUE_OUT_OF_RANGE,"입력한 위도 혹은 경도 값이 범위를 초과하거나 미만입니다. 범위를 재점검해주십시오.","/members");
        }

        // 2. createNewPoint() : dto의 위도, 경도에 따른 point 객체 새로 생성
        Point newLocation = createNewPoint(updateRequestDto.getLatitude(), updateRequestDto.getLongitude());

        // 3. 사용자 프로필 url imgUrl 변수 초기화
        String imgUrl=member.getProfileImgUrl();

        // 사용자가 이미지 사진을 변경할 경우
        // uploadNewImageToS3() : S3에 업로드 후 imgUrl 변수에 저장
        if (imgFile!=null){
            imgUrl = uploadNewImageToS3(imgFile);
        }

        // 4. updateMemberProfile 함수로 member 정보 수정
        member.updateMemberProfile(updateRequestDto,newLocation,imgUrl);

        // 5. 수정된 내용으로 회원 정보를 db에 다시 저장
        Member updatedMember = memberRepository.save(member);

        // 6. MemberProfileResponseDto return
        return MemberProfileResponseDto.builder()
                .id(updatedMember.getId())
                .email(updatedMember.getEmail())
                .nickname(updatedMember.getNickname())
                .profileImg(updatedMember.getProfileImgUrl())
                .location(LocationResponseDtoComponent.builder()
                        .addressSt(updatedMember.getAddressSt())
                        .addressDetail(updatedMember.getAddressDetail())
                        .latitude(updatedMember.getLocationPoint().getY())
                        .longitude(updatedMember.getLocationPoint().getX())
                        .build())
                .provider(updatedMember.getProvider().name())
                .joinedAt(updatedMember.getCreatedAt())
                .recentModifiedAt(updatedMember.getModifiedAt())
                .build();

    }

    /* 회원의 isAvailKeyword를 변경 */
    public AvailResponseDto updateAvailKeyword(Member member, Boolean keyword) {
        member.updateAvailKeyword(keyword);
        Member updatedMember = memberRepository.save(member);

        return AvailResponseDto.builder()
                .id(updatedMember.getId())
                .isKeywordAvail(updatedMember.getIsKeywordAvail())
                .isNoticeAvail(updatedMember.getIsNoticeAvail())
                .build();
    }

    /* 회원의 isAvailNotice를 변경 */
    public AvailResponseDto updateAvailNotice(Member member, Boolean notice) {
        member.updateAvailNotice(notice);
        Member updatedMember = memberRepository.save(member);

        // notice가 True일 경우 sseService에 client 추가
        if(notice){
            sseService.registerClient(member.getId());
        // notice가 False일 경우 sseService에 client 제외
        }else{
            sseService.unregisterClient(member.getId());
        }

        return AvailResponseDto.builder()
                .id(updatedMember.getId())
                .isKeywordAvail(updatedMember.getIsKeywordAvail())
                .isNoticeAvail(updatedMember.getIsNoticeAvail())
                .build();

    }

    /* 회원 탈퇴 */
    public MemberWithdrawalResponseDto withdrawal(Member member) {

        // sseService client에서 삭제
        sseService.unregisterClient(member.getId());

        // 이미지 url 삭제 로직
        if(member.getProfileImgUrl()!=null){
            String profileImgUrl = member.getProfileImgUrl();
            // S3_URL 패턴에 맞지 않으면 INVALID S3 URL error throw
            if (!isValidS3Url(profileImgUrl)){
                throw new AppException(ErrorCode.INVALID_S3_URL,"S3 URL 형식에 맞지 않습니다.","/members");
            }

            // key 추출
            String objectKey = URI.create(profileImgUrl)
                    .getPath().substring(1);

            // s3 버킷에서 삭제
            s3Client.deleteObject(bucketName,objectKey);
        }

        // kakao 연결 끊기
        disconnectKakaoRegistration(member.getAccessId(),member.getAccessToken());

        MemberWithdrawalResponseDto responseDto = MemberWithdrawalResponseDto.builder()
                        .id(member.getId())
                        .nickname(member.getNickname())
                        .email(member.getEmail())
                        .build();

        memberRepository.deleteById(member.getId());

        return responseDto;

    }

    /* 회원 나눔 현황 조회 */
    public MemberSharingStatusResponseDto findMemberSharingStatus(Member writer) {

        // writer의 나눔 현황 리스트
        MemberSharingStatusResponseComponent statusDto = getSharingStatusResponseComponent(writer);

        // writer dto
        MemberCompletedProfileResponseComponent writerDto = MemberCompletedProfileResponseComponent.builder()
                .id(writer.getId())
                .email(writer.getEmail())
                .imgUrl(writer.getProfileImgUrl())
                .nickname(writer.getNickname())
                .sharingTotal(sharingPostRepository.countByWriter(writer))
                .build();

        return MemberSharingStatusResponseDto.builder()
                .writer(writerDto)
                .statusByCategory(statusDto)
                .build();

    }

    /* 회원 탈퇴 시 카카오 연결 끊기 */
    public void disconnectKakaoRegistration(Long accessId, String accessToken){
        WebClient webClient = WebClient.builder()
                .baseUrl("https://kapi.kakao.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();

        webClient
                .post()
                .uri(uriBuilder -> uriBuilder.path("/v1/user/unlink").build())
                .header("Authorization","Bearer "+accessToken)
                .retrieve();
    }


    /* MemberService private 함수 : S3에 새 이미지 업로드 후 URL 문자열 반환 */
    public String uploadNewImageToS3(MultipartFile imgFile){
        String key = "images/" + UUID.randomUUID() + "_" + imgFile.getOriginalFilename();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(imgFile.getSize());
        metadata.setContentType(imgFile.getContentType());

        try (InputStream inputStream = imgFile.getInputStream()){
            s3Client.putObject(bucketName,key,inputStream,metadata);
        }
        catch (IOException e){
            throw new AppException(ErrorCode.AWS_S3_IMG_UPLOAD_CONNECTION_ERROR, "sharing post create - POST error","/sharing");
        }

        return s3Client.getUrl(bucketName, key).toString();

    }

    /* MemberService private 함수 : 새로운 위도와 경도로 Point 객체 새로 생성 */
    private Point createNewPoint(double latitude, double longitude){
        Point newPoint = new GeometryFactory().createPoint(new Coordinate(longitude, latitude));
        newPoint.setSRID(4326);

        return newPoint;
    }

    /* 회원의 나눔글 현황 리스트 반환 함수 */
    private MemberSharingStatusResponseComponent getSharingStatusResponseComponent(Member writer){
        Long BAKERY =0l;
        Long BEVERAGE=0l;
        Long CONVENIENCEFOOD=0l;
        Long KOREAN=0l;
        Long JAPANESE=0l;
        Long CHINESE=0l;
        Long WESTERN=0l;
        Long SNACK=0l;
        Long GROCERIES=0l;
        Long ETC=0l;

        List<Tuple> objects = sharingPostRepository.countByCategoryForWriter(writer);
        for (Tuple obj : objects){
            PostCategory category = (PostCategory) obj.get(0,PostCategory.class);
            Long count = (Long) obj.get(1, Long.class);

            switch (category){
                case BAKERY -> BAKERY=count;
                case BEVERAGE -> BEVERAGE = count;
                case CONVENIENCE_FOOD -> CONVENIENCEFOOD = count;
                case KOREAN -> KOREAN = count;
                case JAPANESE -> JAPANESE = count;
                case CHINESE -> CHINESE = count;
                case WESTERN -> WESTERN = count;
                case SNACK -> SNACK = count;
                case GROCERIES -> GROCERIES = count;
                case ETC -> ETC = count;
            }


        }

        return MemberSharingStatusResponseComponent.builder()
                .BAKERY(BAKERY)
                .BEVERAGE(BEVERAGE)
                .CONVENIENCEFOOD(CONVENIENCEFOOD)
                .KOREAN(KOREAN)
                .JAPANESE(JAPANESE)
                .CHINESE(CHINESE)
                .WESTERN(WESTERN)
                .SNACK(SNACK)
                .GROCERIES(GROCERIES)
                .ETC(ETC)
                .build();

    }

    /* s3 URL Pattern 확인 */
    private Boolean isValidS3Url(String url){
        // 정규 표현식 패턴
        final String S3_URL_PATTERN = "^https://(.+)/([^/]+)/(.+)$";
        Pattern pattern = Pattern.compile(S3_URL_PATTERN);
        Matcher matcher = pattern.matcher(url);

        return matcher.matches();
    }


    /*채팅 - 상대방 프로필 조회 */
    public OpponentInfoResponseDto findOpponentInfo(Long opponentId) {
        Member opponent = memberRepository.findById(opponentId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_MEMBER, "opponent profile - GET error", "/members/"+opponentId));

        return OpponentInfoResponseDto.builder()
                .id(opponent.getId())
                .nickname(opponent.getNickname())
                .profileImg(opponent.getProfileImgUrl())
                .build();

    }
}
