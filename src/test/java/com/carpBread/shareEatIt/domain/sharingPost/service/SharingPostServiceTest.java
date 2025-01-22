package com.carpBread.shareEatIt.domain.sharingPost.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.carpBread.shareEatIt.config.GeometryFactoryConfig;
import com.carpBread.shareEatIt.config.S3TestConfig;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.entity.Provider;
import com.carpBread.shareEatIt.domain.participation.entity.Participation;
import com.carpBread.shareEatIt.domain.participation.repository.GratitudeStickerRepository;
import com.carpBread.shareEatIt.domain.participation.repository.ParticipationRepository;
import com.carpBread.shareEatIt.domain.sharingPost.dto.SharingPostResponseDto;
import com.carpBread.shareEatIt.domain.sharingPost.dto.SharingPostUpdateRequestDto;
import com.carpBread.shareEatIt.domain.sharingPost.entity.PostCategory;
import com.carpBread.shareEatIt.domain.sharingPost.entity.PostStatus;
import com.carpBread.shareEatIt.domain.sharingPost.entity.PostType;
import com.carpBread.shareEatIt.domain.sharingPost.entity.SharingPost;
import com.carpBread.shareEatIt.domain.sharingPost.repository.PostImgUrlRepository;
import com.carpBread.shareEatIt.domain.sharingPost.repository.SharingPostRepository;
import com.carpBread.shareEatIt.global.exception.CustomException;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/* SharingPostService.java Service 단위 테스트 */

@ExtendWith(MockitoExtension.class)
@Import({S3TestConfig.class, GeometryFactoryConfig.class})
class SharingPostServiceTest {

    @InjectMocks
    private SharingPostService sharingPostService;

    @Mock
    private SharingPostRepository sharingPostRepository;

    @Mock
    private GratitudeStickerRepository gratitudeStickerRepository;

    @Mock
    private ParticipationRepository participationRepository;
    @Mock
    private PostImgUrlRepository postImgUrlRepository;
    @Mock
    private GeometryFactory geometryFactory;

    @Mock
    private AmazonS3 s3Client;

    @Test
    @DisplayName("성공 : 나눔글 내용 수정 성공 단위테스트")
    void updateSharingPostSuccessTest() throws IOException{
        // given
        // 1. testMember 생성
        Member testMember = createTestMember(1L, Provider.INDIVIDUAL);

        // 2. sharingPostUpdateRequestDto 생성
        SharingPostUpdateRequestDto requestDto = generateUpdateRequestDto("INDIVIDUAL");

        // 3. imgList 생성
        List<MultipartFile> imgFiles=new ArrayList<>();
        imgFiles.add(generateMockMultipartFile());

        // 4. s3Client mock 설정
        Mockito.doReturn(null).when(s3Client).putObject(
                Mockito.anyString(), // 버킷명
                Mockito.anyString(), // 키
                Mockito.any(InputStream.class), // inputstream
                Mockito.any(ObjectMetadata.class) // metadata
        );

        Mockito.when(s3Client.getUrl(Mockito.anyString(),Mockito.anyString()))
                .thenReturn(new URL("https://mock-s3-url.com/test.jpg"));

        ReflectionTestUtils.setField(sharingPostService, "bucketName", "shareeat-github-actions-s3-bucket");

        // 5. sharingPostRepository.findById 리턴 값 지정
        Point point = generatePoint();
        SharingPost post = generateSharingPost(PostType.INDIVIDUAL, point, testMember, PostStatus.AVAILABLE);
        Optional<SharingPost> optionalSharingPost = Optional.of(post);

        Mockito.doReturn(optionalSharingPost).when(sharingPostRepository)
                .findById(Mockito.any());

        // 6. geometryFactory createPoint return 값 지정
        Mockito.when(geometryFactory.createPoint((Coordinate) Mockito.any()))
                .thenReturn(point);

        // 7. sharingPostRepository return 값 지정
        Mockito.when(sharingPostRepository.save(Mockito.any()))
                .thenReturn(post);

        // 8. postImgUrlRepository save return 값 지정
        Mockito.doReturn(null).when(postImgUrlRepository)
                .save(Mockito.any());

        // 9. gratitudeStickerRepository.existsByPost return 값 지정
        Mockito.doReturn(false).when(gratitudeStickerRepository)
                .existsByPost(Mockito.any());

        // 10. participationRepository.findByPostIdAndStatus return 값 지정
        Mockito.doReturn(new ArrayList<Participation>()).when(participationRepository)
                .findByPostIdAndStatus(Mockito.any());

        // when
        SharingPostResponseDto responseDto = sharingPostService.updateSharingPost(testMember, 1L, imgFiles, requestDto);

        // then
        assertThat(responseDto.getGratitudeSticker()).isEqualTo(null);
        assertThat(responseDto.getLocation().getAddressDetail()).isEqualTo(requestDto.getAddressDetail());

    }

    @Test
    @DisplayName("실패 : 작성자가 아닐 경우 나눔글 내용 수정 실패 단위테스트")
    void UnauthorizedMemberUpdateSharingPostFailTest() throws IOException{
        // given
        // 1. testMember 생성
        Member testMember = createTestMember(2L, Provider.INDIVIDUAL);
        Member writer = createTestMember(1L,Provider.INDIVIDUAL);

        // 2. sharingPostUpdateRequestDto 생성
        SharingPostUpdateRequestDto requestDto = generateUpdateRequestDto("INDIVIDUAL");

        // 3. imgList 생성
        List<MultipartFile> imgFiles=new ArrayList<>();
        imgFiles.add(generateMockMultipartFile());

        // 4. sharingPostRepository return 값 지정
        Point point = generatePoint();
        SharingPost post = generateSharingPost(PostType.INDIVIDUAL, point, writer, PostStatus.AVAILABLE);
        Optional<SharingPost> optionalSharingPost = Optional.of(post);
        Mockito.doReturn(optionalSharingPost).when(sharingPostRepository)
                .findById(Mockito.any());

        // when & then
        CustomException thrownException = assertThrows(CustomException.class, () ->
                sharingPostService.updateSharingPost(testMember,1L, imgFiles, requestDto));
//        assertThat(thrownException.getCustomExceptionStatus().getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);

    }

    @Test
    @DisplayName("실패 : 참여 및 평가가 완료된 나눔글일 경우 나눔글 내용 수정 실패 단위테스트")
    void AlreadyFinishedPostUpdateSharingPostFailTest() throws IOException{
        // given
        // 1. testMember 생성
        Member testMember = createTestMember(1L, Provider.INDIVIDUAL);

        // 2. sharingPostUpdateRequestDto 생성
        SharingPostUpdateRequestDto requestDto = generateUpdateRequestDto("INDIVIDUAL");

        // 3. imgList 생성
        List<MultipartFile> imgFiles=new ArrayList<>();
        imgFiles.add(generateMockMultipartFile());

        // 4. participationRepository.findByPostIdAndStatus return 값 지정
        Mockito.doReturn(new ArrayList<Participation>()).when(participationRepository)
                .findByPostIdAndStatus(Mockito.any());

        // 5. sharingPostRepository return 값 지정
        Point point = generatePoint();
        SharingPost post = generateSharingPost(PostType.INDIVIDUAL, point, testMember, PostStatus.COMPLETED);
        Optional<SharingPost> optionalSharingPost = Optional.of(post);

        Mockito.doReturn(optionalSharingPost).when(sharingPostRepository)
                .findById(Mockito.any());

        // when & then
        CustomException thrownException = assertThrows(CustomException.class, () ->
                sharingPostService.updateSharingPost(testMember,1L, imgFiles, requestDto));
//        assertThat(thrownException.getCustomExceptionStatus().getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("실패 : 사용자가 INDIVIDUAL PROVIDER인데 나눔글 TYPE을 STORE로 바꾸려고 할 경우 나눔글 내용 수정 실패 단위테스트")
    void InvalidMemberProviderPostUpdateSharingPostFailTest() throws IOException{
        // given
        // 1. testMember 생성
        Member testMember = createTestMember(1L, Provider.INDIVIDUAL);

        // 2. sharingPostUpdateRequestDto 생성
        SharingPostUpdateRequestDto requestDto = generateUpdateRequestDto("STORE");

        // 3. imgList 생성
        List<MultipartFile> imgFiles=new ArrayList<>();
        imgFiles.add(generateMockMultipartFile());

        // 4. participationRepository.findByPostIdAndStatus return 값 지정
        Mockito.doReturn(new ArrayList<Participation>()).when(participationRepository)
                .findByPostIdAndStatus(Mockito.any());

        // 5. sharingPostRepository return 값 지정
        Point point = generatePoint();
        SharingPost post = generateSharingPost(PostType.INDIVIDUAL, point, testMember, PostStatus.AVAILABLE);
        Optional<SharingPost> optionalSharingPost = Optional.of(post);
        Mockito.doReturn(optionalSharingPost).when(sharingPostRepository)
                .findById(Mockito.any());

        // when & then
        CustomException thrownException = assertThrows(CustomException.class, () ->
                sharingPostService.updateSharingPost(testMember,1L, imgFiles, requestDto));
//        assertThat(thrownException.getCustomExceptionStatus().getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("실패 : 당사자가 아닐 경우 나눔글 삭제 불가 실패 단위 테스트")
    void deleteSharingPost() {
        // given
        // 1. test member 생성
        Member testMember = createTestMember(2L, Provider.INDIVIDUAL);
        Member writer = createTestMember(1L, Provider.INDIVIDUAL);


        // 2. sharingPostRepository findById 리턴 값 지정
        Point point = generatePoint();
        SharingPost post = generateSharingPost(PostType.INDIVIDUAL, point, writer, PostStatus.AVAILABLE);
        Optional<SharingPost> optionalSharingPost = Optional.of(post);

        Mockito.doReturn(optionalSharingPost).when(sharingPostRepository)
                .findById(Mockito.any());

        // when & then
        CustomException thrownException = assertThrows(CustomException.class, () ->
                sharingPostService.deleteSharingPost(testMember,2L));
//        assertThat(thrownException.getCustomExceptionStatus().getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);

    }


    /* TEST MEMBER 객체 생성 */
    private Member createTestMember(Long id, Provider provider){
        Point memberLocation =new GeometryFactory().createPoint(
                new Coordinate(127.099491,36.798330)
        );
        memberLocation.setSRID(4326);
        return Member.builder()
                .id(id)
                .nickname("test1")
                .email("test@gmail.com")
                .isKeywordAvail(true)
                .isNoticeAvail(true)
                .profileImgUrl("https://shareeat-github-actions-s3-bucket.s3.ap-northeast-2.amazonaws.com/images/c97250ae-0b71-4983-a131-56f0c3bd57b4_unnamed.jpg")
                .locationPoint(memberLocation)
                .addressSt("addressSt")
                .addressDetail("addressDetail")
                .provider(provider)
                .build();
    }



    /* mock SharingPost 생성 */
    private SharingPost generateSharingPost(PostType postType,
                                            Point point,
                                            Member member, PostStatus postStatus) {

        return SharingPost.builder()
                .title("나눔글 제목 1")
                .category(PostCategory.BAKERY)
                .isFinished(true)
                .foodName("음식 이름 1")
                .expDate(LocalDate.now())
                .purchaseDate(LocalDate.now())
                .addressSt("장소 1")
                .addressDetail("상세 장소 1")
                .kakaoLocationCode("1111111")
                .locationPoint(point)
                .description("상세 설명")
                .postType(postType)
                .status(postStatus)
                .writer(member)
                .noticed(false)
                .endAt(LocalDateTime.now())
                .build();
    }

    /* mockMultipartFile 생성 */
    private MockMultipartFile generateMockMultipartFile() throws IOException {
        String filePath = "src/test/resources/073d2624-b839-4d79-8b29-4dddc8455498.jpg";
        File file = new File(filePath);
        FileInputStream fileInputStream = new FileInputStream(file);

        return new MockMultipartFile(
                "file",
                file.getName(),
                "image/jpeg",
                fileInputStream
        );
    }

    /* sharingpostupdateRequestDto 생성 */
    private SharingPostUpdateRequestDto generateUpdateRequestDto(String postType){
        return new SharingPostUpdateRequestDto("나눔글 제목 1",PostCategory.BAKERY.name(),
                true,"음식 이름 1",LocalDate.now(),LocalDate.now(),
                "장소 1","상세 장소 1","1111111",36.798331,127.099492,new ArrayList<>(),
                "상세 설명", postType, LocalDateTime.now());

    }

    /* Point 객체 생성 */
    private Point generatePoint(){
        Point memberLocation =new GeometryFactory().createPoint(
                new Coordinate(127.099492,36.798331)
        );
        memberLocation.setSRID(4326);
        return memberLocation;
    }


//    @Test
//    void getPostImgUrlList() {
//    }
//
//    @Test
//    void updatePostImgList() {
//    }
}