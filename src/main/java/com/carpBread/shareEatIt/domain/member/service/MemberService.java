package com.carpBread.shareEatIt.domain.member.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.carpBread.shareEatIt.domain.member.dto.*;
import com.carpBread.shareEatIt.domain.member.dto.request.MemberProfileUpdateRequestDto;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.repository.MemberRepository;
import com.carpBread.shareEatIt.domain.notice.service.SseService;
import com.carpBread.shareEatIt.domain.participation.repository.GratitudeStickerRepository;
import com.carpBread.shareEatIt.domain.sharingPost.entity.PostCategory;
import com.carpBread.shareEatIt.domain.sharingPost.repository.SharingPostRepository;
import com.carpBread.shareEatIt.global.exception.AppException;
import com.carpBread.shareEatIt.global.exception.ErrorCode;
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

@Service
@Slf4j @Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final GratitudeStickerRepository gratitudeStickerRepository;
    private final SharingPostRepository sharingPostRepository;
    private final SseService sseService;
    private final AmazonS3 s3Client;

    private final GeometryFactory geometryFactory = new GeometryFactory();


    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    /* 멤버의 스티커 현황 찾기 */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public MemberStickerResponseDto findStickers(Long memberId){
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_MEMBER, "member profile - GET error", "/members/stickers"));

        List<Object[]> stickers = gratitudeStickerRepository.countByGratitudeTypeByGiver(findMember.getId());
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
                .id(findMember.getId())
                .profileImg(findMember.getProfileImgUrl())
                .nickname(findMember.getNickname())
                .email(findMember.getEmail())
                .stickers(stickersDto)
                .isKeywordAvail(findMember.getIsKeywordAvail())
                .isNoticeAvail(findMember.getIsNoticeAvail())
                .provider(findMember.getProvider().name())
                .build();

    }

    public MemberProfileResponseDto updateProfile(Long memberId, MultipartFile imgFile, MemberProfileUpdateRequestDto updateRequestDto) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_MEMBER,
                        "member profile update - PUT error", "/members"));

        // 점검 : MySQL 8.4 Reference Manual 에 정의된 메뉴얼에 따라, latitude(위도)는 [-90.0, 90.0] / longitude(경도)는 [-180.0, 180.0] 범위로 지정
        if ((updateRequestDto.getLatitude()>90.0 || updateRequestDto.getLatitude()<-90.0)
                || (updateRequestDto.getLongitude()>180.0 || updateRequestDto.getLongitude()<-180.0)){
            throw new AppException(ErrorCode.VALUE_OUT_OF_RANGE,"입력한 위도 혹은 경도 값이 범위를 초과하거나 미만입니다. 범위를 재점검해주십시오.","/members");
        }

        Point point = geometryFactory.createPoint(new Coordinate(updateRequestDto.getLongitude(), updateRequestDto.getLatitude()));
        point.setSRID(4326);

        String imgUrl=findMember.getProfileImgUrl();


        if (!imgFile.isEmpty()){

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

            imgUrl = s3Client.getUrl(bucketName, key).toString();


        }



        findMember.updateMemberProfile(updateRequestDto,point,imgUrl);
        Member updatedMember = memberRepository.save(findMember);


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

    public AvailResponseDto updateAvailKeyword(Member member, Boolean keyword) {
        member.updateAvailKeyword(keyword);
        Member updatedMember = memberRepository.save(member);

        return AvailResponseDto.builder()
                .id(updatedMember.getId())
                .isKeywordAvail(updatedMember.getIsKeywordAvail())
                .isNoticeAvail(updatedMember.getIsNoticeAvail())
                .build();
    }

    // notice avail 설정 변경
    public AvailResponseDto updateAvailNotice(Member member, Boolean notice) {
        member.updateAvailNotice(notice);
        Member updatedMember = memberRepository.save(member);

        if(notice){
            sseService.registerClient(member.getId());
        }else{
            sseService.unregisterClient(member.getId());
        }

        return AvailResponseDto.builder()
                .id(updatedMember.getId())
                .isKeywordAvail(updatedMember.getIsKeywordAvail())
                .isNoticeAvail(updatedMember.getIsNoticeAvail())
                .build();

    }



    public MemberWithdrawalResponseDto withdrawal(Long memberId) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_MEMBER,
                        "member withdrawal - DELETE error", "/members"));

        sseService.unregisterClient(memberId);

        // 이미지 url 삭제 로직 추가 예정
        String profileImgUrl = findMember.getProfileImgUrl();
        String objectKey = URI.create(profileImgUrl)
                .getPath().substring(1);
        s3Client.deleteObject(bucketName,objectKey);


        // kakao 연결 끊기
        disconnectKakaoRegistration(findMember.getAccessId(),findMember.getAccessToken());

        MemberWithdrawalResponseDto responseDto = MemberWithdrawalResponseDto.builder()
                        .id(findMember.getId())
                        .nickname(findMember.getNickname())
                        .email(findMember.getEmail())
                        .build();

        memberRepository.deleteById(memberId);

        return responseDto;

    }

    private void disconnectKakaoRegistration(Long accessId, String accessToken){
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


    public MemberSharingStatusResponseDto findMemberSharingStatus(Member writer) {

        MemberSharingStatusResponseComponent statusDto = getSharingStatusResponseComponent(writer);

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

        List<Object[]> objects = sharingPostRepository.countByCategoryForWriter(writer);
        for (Object[] obj : objects){
            PostCategory category = (PostCategory) obj[0];
            Long count = (Long) obj[1];

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
