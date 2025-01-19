package com.carpBread.shareEatIt.domain.member.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;

import static org.assertj.core.api.Assertions.assertThat;
import com.carpBread.shareEatIt.config.S3TestConfig;
import com.carpBread.shareEatIt.domain.member.dto.response.AvailResponseDto;
import com.carpBread.shareEatIt.domain.member.dto.response.MemberProfileResponseDto;
import com.carpBread.shareEatIt.domain.member.dto.response.MemberSharingStatusResponseDto;
import com.carpBread.shareEatIt.domain.member.dto.response.MemberStickerResponseDto;
import com.carpBread.shareEatIt.domain.member.dto.request.MemberProfileUpdateRequestDto;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.entity.Provider;
import com.carpBread.shareEatIt.domain.member.repository.MemberRepository;
import com.carpBread.shareEatIt.domain.notice.service.SseService;
import com.carpBread.shareEatIt.domain.participation.repository.GratitudeStickerRepository;
import com.carpBread.shareEatIt.domain.sharingPost.entity.PostCategory;
import com.carpBread.shareEatIt.domain.sharingPost.repository.SharingPostRepository;
import com.carpBread.shareEatIt.global.exception.AppException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static reactor.core.publisher.Mono.when;

/* MemberService.java Service 단위 테스트 */
@ExtendWith(MockitoExtension.class)
@Import(S3TestConfig.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private SharingPostRepository sharingPostRepository;

    @Mock
    private GratitudeStickerRepository gratitudeStickerRepository;

    @Mock
    private AmazonS3 s3Client;

    @Mock
    private SseService sseService;

    @InjectMocks
    private MemberService memberService;

    /* 회원 스티커 현황 조회 */
    @Test
    @DisplayName("성공 : 회원의 스티커 현황 조회 성공 테스트")
    void findMemberStickersSuccessTest() {
        // given
        // 1. testMember 생성
        Member testMember = createTestMember();

        // 2. gratitudeStickerRepository.countByGratitudeTypeByGiver return 값 지정
        List<Object> stickers = Arrays.asList(new Long[]{1L,2L,3L,4L,5L});
        Mockito.doReturn(stickers).when(gratitudeStickerRepository)
                .countByGratitudeTypeByGiver(Mockito.any());

        // when
        MemberStickerResponseDto responseDto = memberService.findStickers(testMember);

        // then
        assertThat(testMember.getId()).isEqualTo(responseDto.getId());
        assertThat(testMember.getEmail()).isEqualTo(responseDto.getEmail());
        assertThat(stickers.get(2)).isEqualTo(responseDto.getStickers().getSmile2());

    }

    @Test
    @DisplayName("성공 : 회원 정보 수정 성공 테스트")
    void updateMemberProfileSuccessTest() throws IOException {
        // given
        Member testMember = createTestMember();

        //// 함수 매개변수
        String filePath = "src/test/resources/073d2624-b839-4d79-8b29-4dddc8455498.jpg";
        File file = new File(filePath);
        FileInputStream fileInputStream = new FileInputStream(file);

        MultipartFile newImage = new MockMultipartFile(
                "file",
                file.getName(),
                "image/jpeg",
                fileInputStream
        );

        MemberProfileUpdateRequestDto requestDto = MemberProfileUpdateRequestDto.builder()
                .profileImg("testimgurl")
                .nickname("test22")
                .provider("STORE")
                .latitude(36.798331)
                .longitude(127.099492)
                .addressSt("충청남도 수한군 행복동")
                .addressDetail("사랑길 56번지")
                .build();

        //// 다른 함수 모듈의 return 값 지정 - stub 들의 return 값 지정
        // 1. service 클래스의 uploadNewImageToS3 함수의 return 값을 지정
        // 1) s3Client mock 설정
        Mockito.doReturn(null).when(s3Client).putObject(
                Mockito.anyString(), // 버킷명
                Mockito.anyString(), // 키
                Mockito.any(InputStream.class), // inputstream
                Mockito.any(ObjectMetadata.class) // metadata
        );

        Mockito.when(s3Client.getUrl(Mockito.anyString(),Mockito.anyString()))
                .thenReturn(new URL("https://mock-s3-url.com/test.jpg"));

        ReflectionTestUtils.setField(memberService, "bucketName", "shareeat-github-actions-s3-bucket");

        // 2. repository의 save 함수 리턴값 지정
        Point memberLocation =new GeometryFactory().createPoint(
                new Coordinate(127.099492,36.798331)
        );
        memberLocation.setSRID(4326);

        Mockito.when(memberRepository.save(Mockito.any()))
                .thenReturn(Member.builder()
                        .id(1L).nickname("test22").email("test@gmail.com")
                        .isKeywordAvail(true).isNoticeAvail(true).locationPoint(memberLocation)
                        .addressSt("충청남도 수한군 행복동").addressDetail("사랑길 56번지")
                        .provider(Provider.STORE).createdAt(LocalDateTime.now()).modifiedAt(LocalDateTime.now())
                        .profileImgUrl("https://mock-s3-url.com/test.jpg").build()
                );

        // when
        MemberProfileResponseDto responseDto = memberService.updateProfile(testMember, newImage, requestDto);

        // then
        assertEquals(testMember.getNickname(),requestDto.getNickname());
        assertEquals(testMember.getAddressSt(),requestDto.getAddressSt());
        assertEquals(testMember.getProfileImgUrl(),"https://mock-s3-url.com/test.jpg");
        assertEquals(testMember.getLocationPoint().getY(), responseDto.getLocation().getLatitude());

    }

    @Test
    @DisplayName("실패 : 사용자 정보 수정 시 dto의 latitude와 longitude 범위가 맞지 않을 때 AppException 리턴하는 실패 테스트")
    void updateMemberProfileFailReturnAppExceptionTest() throws IOException{
        // given
        Member testMember = createTestMember();

        //// 함수 매개변수
        String filePath = "src/test/resources/073d2624-b839-4d79-8b29-4dddc8455498.jpg";
        File file = new File(filePath);
        FileInputStream fileInputStream = new FileInputStream(file);

        MultipartFile newImage = new MockMultipartFile(
                "file",
                file.getName(),
                "image/jpeg",
                fileInputStream
        );

        MemberProfileUpdateRequestDto requestDto = MemberProfileUpdateRequestDto.builder()
                .profileImg("testimgurl")
                .nickname("test22")
                .provider("STORE")
                .latitude(127.099492)
                .longitude(36.798331)
                .addressSt("충청남도 수한군 행복동")
                .addressDetail("사랑길 56번지")
                .build();

        // when / then
        AppException thrownException = assertThrows(AppException.class, () -> {
            memberService.updateProfile(testMember, newImage, requestDto);
        });

        assertThat(thrownException.getErrorCode().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);

    }

    /* 회원의 isAvailKeyword를 변경 */
    @Test
    @DisplayName("성공 : 사용자의 availKeyword를 true에서 false로 바뀌는 성공 테스트")
    void updateAvailKeyword() {
        // given
        // 1. testMember 생성
        Member testMember = createTestMember();
        Boolean isKeyword = false;

        // 2. memberRepository.save 함수 return 값 지정
        Point memberLocation =new GeometryFactory().createPoint(
                new Coordinate(127.099491,36.798330)
        );
        Member returnMember = Member.builder()
                .id(1L)
                .nickname("test1")
                .email("test@gmail.com")
                .isKeywordAvail(isKeyword)
                .isNoticeAvail(true)
                .profileImgUrl("testimgurl")
                .locationPoint(memberLocation)
                .addressSt("addressSt")
                .addressDetail("addressDetail")
                .provider(Provider.STORE)
                .build();
        Mockito.doReturn(returnMember)
                .when(memberRepository)
                .save(Mockito.any());

        // when
        memberService.updateAvailKeyword(testMember, isKeyword);

        // then
        assertThat(testMember.getIsKeywordAvail()).isEqualTo(isKeyword);

    }

    /* 회원의 isAvailNotice를 변경 */
    @Test
    @DisplayName("성공 : 사용자의 isAvailNotice를 true에서 false로 바뀌는 성공 테스트")
    void updateMemberAvailNoticeSuccessTest() {
        // given
        // 1. sseService return 값 지정

//        Mockito.doReturn(null).when(sseService)
//                .registerClient(Mockito.any()); // unnecessary

        Mockito.doNothing().when(sseService)
                .unregisterClient(Mockito.any());

        // 2. testMember 생성
        Member testMember = createTestMember();
        Boolean isNotice = false;

        // 3. memberRepository.save 리턴 값 지정
        Point memberLocation =new GeometryFactory().createPoint(
                new Coordinate(127.099491,36.798330)
        );
        Member returnMember = Member.builder()
                .id(1L)
                .nickname("test1")
                .email("test@gmail.com")
                .isKeywordAvail(true)
                .isNoticeAvail(isNotice)
                .profileImgUrl("testimgurl")
                .locationPoint(memberLocation)
                .addressSt("addressSt")
                .addressDetail("addressDetail")
                .provider(Provider.STORE)
                .build();
        Mockito.doReturn(returnMember)
                .when(memberRepository)
                .save(Mockito.any());

        // when
        AvailResponseDto responseDto = memberService.updateAvailNotice(testMember, isNotice);

        // then
        assertThat(responseDto.getIsNoticeAvail()).isEqualTo(isNotice);

    }

    /* 회원 탈퇴 */
    @Test
    @DisplayName("실패 : 회원 탈퇴 시 s3 url이 형식에 맞지 않는 경우 AppException을 반환하는 실패 테스트")
    void memberWithdrawalSuccessTest() {
        // given
        // 1. sseService unregisterClient 리턴 값 지정
        Mockito.doNothing().when(sseService)
                .unregisterClient(Mockito.any());

        // 2. s3Client deleteObject 리턴 값 지정 -> UNNECESSARY
//        ReflectionTestUtils.setField(memberService, "bucketName", "shareeat-github-actions-s3-bucket");
//        Mockito.doNothing().when(s3Client)
//                .deleteObject(Mockito.anyString(), Mockito.any());

        // 3. memberRepository deleteById 리턴 값 지정 -> UNNECESSARY
//        Mockito.doNothing().when(memberRepository)
//                .deleteById(Mockito.any());

        // 4. testMember 생성
        Point memberLocation =new GeometryFactory().createPoint(
                new Coordinate(127.099491,36.798330)
        );
        memberLocation.setSRID(4326);
        Member member = Member.builder()
                .id(1L)
                .nickname("test1")
                .email("test@gmail.com")
                .isKeywordAvail(true)
                .isNoticeAvail(true)
                .profileImgUrl("profileImgUrl")
                .locationPoint(memberLocation)
                .addressSt("addressSt")
                .addressDetail("addressDetail")
                .provider(Provider.STORE)
                .build();

        // when / then
        AppException thrownException = assertThrows(AppException.class, () -> {
            memberService.withdrawal(member);
        });

        assertThat(thrownException.getErrorCode().getStatus()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);

    }

    /* 회원 나눔 현황 조회 */
    @Test
    @DisplayName("성공 : 회원 나눔 현황 조회 성공 테스트")
    void findMemberSharingStatusSuccessTest() {
        // given
        // 1. testMember
        Member testMember = createTestMember();


        // 2. sharingPostRepository 함수 리턴 값 지정
        Object[] listElement1 = {PostCategory.BAKERY,1L};
        Object[] listElement2 = {PostCategory.CHINESE,2L};

        List<Object[]> postLists = Arrays.asList(new Object[][]{listElement1, listElement2});

        Mockito.doReturn(postLists).when(sharingPostRepository)
                .countByCategoryForWriter(Mockito.any());

        Mockito.doReturn(3L).when(sharingPostRepository)
                .countByWriter(Mockito.any());

        // when
        MemberSharingStatusResponseDto responseDto = memberService.findMemberSharingStatus(testMember);

        // then
        assertThat(responseDto.getStatusByCategory().getCHINESE()).isEqualTo(2L);
        assertThat(responseDto.getStatusByCategory().getBAKERY()).isEqualTo(1L);

        assertThat(responseDto.getWriter().getId()).isEqualTo(testMember.getId());
        assertThat(responseDto.getWriter().getEmail()).isEqualTo(testMember.getEmail());


    }

    private Member createTestMember(){
        Point memberLocation =new GeometryFactory().createPoint(
                new Coordinate(127.099491,36.798330)
        );
        memberLocation.setSRID(4326);
        return Member.builder()
                .id(1L)
                .nickname("test1")
                .email("test@gmail.com")
                .isKeywordAvail(true)
                .isNoticeAvail(true)
                .profileImgUrl("https://shareeat-github-actions-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/c97250ae-0b71-4983-a131-56f0c3bd57b4_unnamed.jpg")
                .locationPoint(memberLocation)
                .addressSt("addressSt")
                .addressDetail("addressDetail")
                .provider(Provider.STORE)
                .build();
    }

}