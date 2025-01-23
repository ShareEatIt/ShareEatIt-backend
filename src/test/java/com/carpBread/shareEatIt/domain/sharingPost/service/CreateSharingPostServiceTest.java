package com.carpBread.shareEatIt.domain.sharingPost.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.carpBread.shareEatIt.config.GeometryFactoryConfig;
import com.carpBread.shareEatIt.config.S3TestConfig;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.entity.Provider;
import com.carpBread.shareEatIt.domain.member.repository.MemberQuerydslRepository;
import com.carpBread.shareEatIt.domain.sharingPost.dto.SharingPostCreateResponseDto;
import com.carpBread.shareEatIt.domain.sharingPost.dto.SharingPostRequestDto;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@Import({S3TestConfig.class, GeometryFactoryConfig.class})
class CreateSharingPostServiceTest {

    @InjectMocks
    private CreateSharingPostService createSharingPostService;

    @Mock
    private SharingPostRepository sharingPostRepository;

    @Mock
    private MemberQuerydslRepository memberQuerydslRepository;

    @Mock
    private PostImgUrlRepository postImgUrlRepository;
    @Mock
    private GeometryFactory geometryFactory;

    @Mock
    private AmazonS3 s3Client;


    @Test
    @DisplayName("성공 : 이미지 있을 때 나눔글 생성 성공 단위 테스트")
    void createSharingPostSuccessTest() throws IOException {
        // given
        // 1. sharingpostrequestdto 생성
        SharingPostRequestDto requestDto = generateSharingPostRequestDto("INDIVIDUAL");

        // 2. test member 객체 생성
        Member testMember = createTestMember(Provider.INDIVIDUAL);

        // 3. imgList
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

        ReflectionTestUtils.setField(createSharingPostService, "bucketName", "shareeat-github-actions-s3-bucket");


        // 5. geometryFactory createPoint return 값 지정
        Point point = generatePoint();
        Mockito.when(geometryFactory.createPoint((Coordinate) Mockito.any()))
                .thenReturn(point);

        // 6. sharingPostRepository return 값 지정
        SharingPost post = generateSharingPost(PostType.INDIVIDUAL, point, testMember);
        Mockito.when(sharingPostRepository.save(Mockito.any()))
                .thenReturn(post);
        Mockito.doReturn(1L)
                .when(sharingPostRepository)
                .countByWriter(Mockito.any());

        // 7. memberQuerydslRepository findMemberWithRadius return 값 지정
        Mockito.when(memberQuerydslRepository.findMemberWithRadius(Mockito.anyDouble(),Mockito.anyDouble(),Mockito.anyDouble()))
                .thenReturn(new ArrayList<>());

        // 8. postImgUrlRepository save return 값 지정
        Mockito.doReturn(null).when(postImgUrlRepository)
                .save(Mockito.any());

        // when
        SharingPostCreateResponseDto sharingPost = createSharingPostService.createSharingPost(imgFiles, requestDto, testMember);

        // then
        assertThat(sharingPost.getId()).isEqualTo(post.getId());
        assertThat(sharingPost.getLocation().getAddressDetail()).isEqualTo(post.getAddressDetail());


    }

    @Test
    @DisplayName("실패 : 사용자가 INDIVIDUAL인데 STORE로 나눔글을 게시하려는 경우 AppException throw 하는 실패 단위 테스트")
    void createSharingPostFailTest() throws IOException{
        // given
        // 1. test member 객체 생성
        Member testMember = createTestMember(Provider.INDIVIDUAL);

        // 2. sharingPostRequestDto 생성
        SharingPostRequestDto requestDto = generateSharingPostRequestDto("STORE");

        // 3. imgList
        List<MultipartFile> imgFiles=new ArrayList<>();
        imgFiles.add(generateMockMultipartFile());

        // when & then
        CustomException thrownException = assertThrows(CustomException.class, () ->
                createSharingPostService.createSharingPost(imgFiles, requestDto, testMember));
//        assertThat(thrownException.getCustomExceptionStatus().getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);

    }


    /* 나눔글 생성 시 request dto 생성 */
    private SharingPostRequestDto generateSharingPostRequestDto(String postType){
        return new SharingPostRequestDto("나눔글 제목 1","BAKERY",
                true,"음식 이름 1",
                LocalDate.now(),LocalDate.now(),"장소 1",
                "상세 장소 1","1111111",
                37.532600,127.024612,
                "상세 설명",postType,LocalDateTime.now());
    }

    /* TEST MEMBER 객체 생성 */
    private Member createTestMember(Provider provider){
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
                .provider(provider)
                .build();
    }

    /* mock SharingPost 생성 */
    private SharingPost generateSharingPost(PostType postType,
                                            Point point,
                                            Member member) {

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
                .status(PostStatus.AVAILABLE)
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

    /* Point 객체 생성 */
    private Point generatePoint(){
        Point memberLocation =new GeometryFactory().createPoint(
                new Coordinate(127.099492,36.798331)
        );
        memberLocation.setSRID(4326);
        return memberLocation;
    }
}