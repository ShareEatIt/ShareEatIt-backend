package com.carpBread.shareEatIt.domain.member.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.carpBread.shareEatIt.domain.member.dto.request.MemberProfileUpdateRequestDto;
import com.carpBread.shareEatIt.domain.member.dto.response.*;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.repository.MemberRepository;
import com.carpBread.shareEatIt.domain.notice.service.SseService;
import com.carpBread.shareEatIt.domain.participation.repository.GratitudeStickerRepository;
import com.carpBread.shareEatIt.domain.sharingPost.entity.PostCategory;
import com.carpBread.shareEatIt.domain.sharingPost.repository.SharingPostRepository;
import com.carpBread.shareEatIt.global.exception.CustomException;
import com.carpBread.shareEatIt.global.exception.CustomExceptionStatus;
import com.carpBread.shareEatIt.global.exception.Domain;
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

        StickersResponseDto stickersDto = new StickersResponseDto(
                responseList.get("SMILE1"),
                responseList.get("SMILE2"),
                responseList.get("SMILE3"),
                responseList.get("SMILE4"),
                responseList.get("SMILE5")
        );

        return new MemberStickerResponseDto(
                member.getId(), member.getProfileImgUrl(),
                member.getNickname(),member.getEmail(),
                stickersDto,member.getIsNoticeAvail(),
                member.getIsKeywordAvail(),
                member.getProvider().name()
        );

    }

    /* 사용자 정보 수정 */
    public MemberProfileResponseDto updateProfile(Member member, MultipartFile imgFile, MemberProfileUpdateRequestDto updateRequestDto) {

        // 1. 점검 : MySQL 8.4 Reference Manual 에 정의된 메뉴얼에 따라, latitude(위도)는 [-90.0, 90.0] / longitude(경도)는 [-180.0, 180.0] 범위로 지정
        if ((updateRequestDto.getLatitude()>90.0 || updateRequestDto.getLatitude()<-90.0)
                || (updateRequestDto.getLongitude()>180.0 || updateRequestDto.getLongitude()<-180.0)){
            throw new CustomException(
                    CustomExceptionStatus.VALUE_OUT_OF_RANGE,
                    "입력한 위도 혹은 경도 값이 범위를 초과하거나 미만입니다. 범위를 재점검해주십시오.",
                    MemberService.class.getName(),
                    "longitude : "+updateRequestDto.getLongitude()+", latitude : "+updateRequestDto.getLatitude(),
                    Domain.MEMBER
            );
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
        LocationResponseDtoComponent locationResponseDtoComponent = new LocationResponseDtoComponent(
                updatedMember.getAddressSt(),
                updatedMember.getAddressDetail(),
                updatedMember.getLocationPoint().getY(),
                updatedMember.getLocationPoint().getX()
        );

        return new MemberProfileResponseDto(
                updatedMember.getId(),
                updatedMember.getProfileImgUrl(),
                updatedMember.getNickname(),
                updatedMember.getEmail(),
                locationResponseDtoComponent,
                updatedMember.getProvider().name(),
                updatedMember.getCreatedAt(),
                updatedMember.getModifiedAt()
        );
    }

    /* 회원의 isAvailKeyword를 변경 */
    public AvailResponseDto updateAvailKeyword(Member member, Boolean keyword) {
        member.updateAvailKeyword(keyword);
        Member updatedMember = memberRepository.save(member);

        return new AvailResponseDto(
                updatedMember.getId(),
                updatedMember.getIsNoticeAvail(),
                updatedMember.getIsKeywordAvail()
        );
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

        return new AvailResponseDto(
                updatedMember.getId(),
                updatedMember.getIsNoticeAvail(),
                updatedMember.getIsKeywordAvail()
        );

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
                throw new CustomException(
                        CustomExceptionStatus.INVALID_S3_URL,
                        "S3 URL 형식에 맞지 않습니다.",
                        MemberService.class.getName(),
                        profileImgUrl,
                        Domain.MEMBER
                );
            }

            // key 추출
            String objectKey = URI.create(profileImgUrl)
                    .getPath().substring(1);

            // s3 버킷에서 삭제
            s3Client.deleteObject(bucketName,objectKey);
        }

        MemberWithdrawalResponseDto responseDto = new MemberWithdrawalResponseDto(
                member.getId(),
                member.getEmail(),
                member.getNickname()
        );

        memberRepository.deleteById(member.getId());

        return responseDto;

    }

    /* 회원 나눔 현황 조회 */
    public MemberSharingStatusResponseDto findMemberSharingStatus(Member writer) {

        // writer의 나눔 현황 리스트
        MemberSharingStatusResponseComponent statusDto = getSharingStatusResponseComponent(writer);

        // writer dto
        MemberCompletedProfileResponseComponent writerDto = new MemberCompletedProfileResponseComponent(
                writer.getId(),
                writer.getEmail(),
                writer.getProfileImgUrl(),
                writer.getNickname(),
                sharingPostRepository.countByWriter(writer)
        );
        return new MemberSharingStatusResponseDto(
          writerDto,
          statusDto
        );
    }

    /* MemberService private 함수 : S3에 새 이미지 업로드 후 URL 문자열 반환 */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String uploadNewImageToS3(MultipartFile imgFile){
        String key = "images/" + UUID.randomUUID() + "_" + imgFile.getOriginalFilename();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(imgFile.getSize());
        metadata.setContentType(imgFile.getContentType());

        try (InputStream inputStream = imgFile.getInputStream()){
            s3Client.putObject(bucketName,key,inputStream,metadata);
        }
        catch (IOException e){
            throw new CustomException(
                    CustomExceptionStatus.AWS_S3_IMG_UPLOAD_CONNECTION_ERROR,
                    "AWS S3 이미지를 업로드 중 서버 내부의 에러가 발생하여 이미지를 S3에 업로드하지 못했습니다. \n Error message : "+e.getMessage(),
                    MemberService.class.getName(),
                    null,
                    Domain.MEMBER
            );
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

        return new MemberSharingStatusResponseComponent(
                BAKERY,BEVERAGE,
                CONVENIENCEFOOD,KOREAN,
                JAPANESE, CHINESE,
                WESTERN, SNACK,
                GROCERIES,ETC
        );

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
                .orElseThrow(() -> new CustomException(
                        CustomExceptionStatus.NOT_FOUND_MEMBER,
                        "상대방 회원 ID로 회원 조회를 하지 못했습니다.",
                        MemberService.class.getName(),
                        opponentId,
                        Domain.MEMBER
                    )
                );

        return new OpponentInfoResponseDto(
                opponent.getId(),
                opponent.getProfileImgUrl(),
                opponent.getNickname()
        );

    }
}
