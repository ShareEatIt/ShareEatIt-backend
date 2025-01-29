package com.carpBread.shareEatIt.domain.report.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.carpBread.shareEatIt.config.S3TestConfig;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.entity.Provider;
import com.carpBread.shareEatIt.domain.report.dto.ReportCreateRequestDto;
import com.carpBread.shareEatIt.domain.report.dto.ReportCreateResponseDto;
import com.carpBread.shareEatIt.domain.report.entity.Report;
import com.carpBread.shareEatIt.domain.report.entity.ReportStatus;
import com.carpBread.shareEatIt.domain.report.repository.ReportRepository;
import com.carpBread.shareEatIt.domain.sharingPost.entity.PostCategory;
import com.carpBread.shareEatIt.domain.sharingPost.entity.PostStatus;
import com.carpBread.shareEatIt.domain.sharingPost.entity.PostType;
import com.carpBread.shareEatIt.domain.sharingPost.entity.SharingPost;
import com.carpBread.shareEatIt.domain.sharingPost.repository.SharingPostRepository;
import com.carpBread.shareEatIt.global.exception.CustomException;
import com.carpBread.shareEatIt.global.exception.CustomExceptionStatus;
import com.carpBread.shareEatIt.global.exception.Domain;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/* ReportService.java Service 단위 테스트 */
@ExtendWith(MockitoExtension.class)
@Import(S3TestConfig.class)
class ReportServiceTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private SharingPostRepository sharingPostRepository;

    @Mock
    private AmazonS3 s3Client;

    @InjectMocks
    private ReportService reportService;

    @Test
    @DisplayName("신고 생성 성공 단위 테스트")
    void createNewReportSuccessTest() throws IOException {
        // given
        // 1. testmember 생성
        Member testMember = createTestMember(1L);

        // 2. 신고 이미지 mock 객체 생성
        MockMultipartFile mockMultipartFile = generateMockMultipartFile();

        // 3. ReportCreateRequestDto 객체 생성
        ReportCreateRequestDto requestDto = new ReportCreateRequestDto(1L, "신고 제목", "신고 내용");

        // 4. sharingPostRepository findById return 값 지정
        Member writer = createTestMember(2L);
        Point point = generatePoint();
        PostType postType = PostType.INDIVIDUAL;
        SharingPost post = generateSharingPost(postType, point, writer);
        Optional<SharingPost> optionalSharingPost = Optional.of(post);
        Mockito.doReturn(optionalSharingPost).when(sharingPostRepository).findById(Mockito.anyLong());

        Mockito.doReturn(false).when(reportRepository).existsByReporterAndPost(Mockito.any(), Mockito.any());

        // 5. s3Client mock 설정
        Mockito.doReturn(null).when(s3Client).putObject(
                Mockito.anyString(), // 버킷명
                Mockito.anyString(), // 키
                Mockito.any(InputStream.class), // inputstream
                Mockito.any(ObjectMetadata.class) // metadata
        );

        Mockito.when(s3Client.getUrl(Mockito.anyString(),Mockito.anyString()))
                .thenReturn(new URL("https://mock-s3-url.com/test.jpg"));

        ReflectionTestUtils.setField(reportService, "bucketName", "shareeat-github-actions-s3-bucket");

        // 6. reportRepository save 함수 return 값 지정
        Report report = new Report(requestDto.getTitle(), requestDto.getContent(),
                ReportStatus.IN_PROGRESS, "https://mock-s3-url.com/test.jpg",
                writer, post);
        Mockito.doReturn(report).when(reportRepository).save(Mockito.any());

        // when
        ReportCreateResponseDto result = reportService.createNewReport(testMember, mockMultipartFile, requestDto);

        // then
        assertThat(result.getImgUrl()).isEqualTo("https://mock-s3-url.com/test.jpg");

    }


    @Test
    @DisplayName("본인의 게시글인 경우 신고 실패 단위테스트")
    void createReportSameWriterWithReporterFailTest() throws IOException{
        // given
        // 1. testmember 생성
        Member testMember = createTestMember(1L);

        // 2. 신고 이미지 mock 객체 생성
        MockMultipartFile mockMultipartFile = generateMockMultipartFile();

        // 3. ReportCreateRequestDto 객체 생성
        ReportCreateRequestDto requestDto = new ReportCreateRequestDto(1L, "신고 제목", "신고 내용");

        // 4. sharingPostRepository findById return 값 지정
        Member writer = createTestMember(1L);
        Point point = generatePoint();
        PostType postType = PostType.INDIVIDUAL;
        SharingPost post = generateSharingPost(postType, point, writer);
        Optional<SharingPost> optionalSharingPost = Optional.of(post);
        Mockito.doReturn(optionalSharingPost).when(sharingPostRepository).findById(Mockito.anyLong());

        // when
        CustomException thrownException = assertThrows(CustomException.class,
                () -> reportService.createNewReport(
                        testMember, mockMultipartFile, requestDto)
        );

        // then
        assertThat(thrownException.getExceptionStatus()).isEqualTo(CustomExceptionStatus.CANNOT_REPORT_SELF);
        assertThat(thrownException.getMessage()).isEqualTo("본인의 게시글을 신고할 수 없습니다");
        assertThat(thrownException.getFilePath()).isEqualTo("ReportService");
        assertThat(thrownException.getTag()).isEqualTo(Domain.REPORT);


    }

    @Test
    @DisplayName("신고 이미지가 없는 경우 신고 실패 단위테스트")
    void createReportWithNoReportImageFailTest() throws IOException{
        // given
        // 1. testmember 생성
        Member testMember = createTestMember(1L);

        // 2. 신고 이미지 mock 객체 생성
        MockMultipartFile mockMultipartFile = null;

        // 3. ReportCreateRequestDto 객체 생성
        ReportCreateRequestDto requestDto = new ReportCreateRequestDto(1L, "신고 제목", "신고 내용");

        // 4. sharingPostRepository findById return 값 지정
        Member writer = createTestMember(2L);
        Point point = generatePoint();
        PostType postType = PostType.INDIVIDUAL;
        SharingPost post = generateSharingPost(postType, point, writer);
        Optional<SharingPost> optionalSharingPost = Optional.of(post);
        Mockito.doReturn(optionalSharingPost).when(sharingPostRepository).findById(Mockito.anyLong());

        Mockito.doReturn(false).when(reportRepository).existsByReporterAndPost(Mockito.any(), Mockito.any());

        // when
        CustomException thrownException = assertThrows(CustomException.class,
                () -> reportService.createNewReport(
                        testMember, mockMultipartFile, requestDto)
        );

        // then
        assertThat(thrownException.getExceptionStatus()).isEqualTo(CustomExceptionStatus.CANNOT_BE_NULL_IMG_FILE_FOR_REPORT);
        assertThat(thrownException.getMessage()).isEqualTo("신고 시 사진 파일은 필수입니다");
        assertThat(thrownException.getFilePath()).isEqualTo("ReportService");
        assertThat(thrownException.getTag()).isEqualTo(Domain.REPORT);


    }

    /* mock SharingPost 생성 */
    private SharingPost generateSharingPost(PostType postType,
                                            Point point,
                                            Member member) {

        return SharingPost.builder()
                .id(1L)
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

    /* test Member 객체 생성 */
    private Member createTestMember(Long id){
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
                .provider(Provider.STORE)
                .build();
    }
}