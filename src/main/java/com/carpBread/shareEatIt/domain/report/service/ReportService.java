package com.carpBread.shareEatIt.domain.report.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.report.dto.ReportCreateRequestDto;
import com.carpBread.shareEatIt.domain.report.dto.ReportCreateResponseDto;
import com.carpBread.shareEatIt.domain.report.dto.ReportMemberResponseComponent;
import com.carpBread.shareEatIt.domain.report.dto.ReportPostResponseComponent;
import com.carpBread.shareEatIt.domain.report.entity.Report;
import com.carpBread.shareEatIt.domain.report.entity.ReportStatus;
import com.carpBread.shareEatIt.domain.report.repository.ReportRepository;
import com.carpBread.shareEatIt.domain.sharingPost.entity.SharingPost;
import com.carpBread.shareEatIt.domain.sharingPost.repository.SharingPostRepository;
import com.carpBread.shareEatIt.global.exception.CustomException;
import com.carpBread.shareEatIt.global.exception.CustomExceptionStatus;
import com.carpBread.shareEatIt.global.exception.Domain;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service @Transactional
@RequiredArgsConstructor
public class ReportService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final ReportRepository reportRepository;
    private final SharingPostRepository sharingPostRepository;

    private final AmazonS3 s3Client;


    public ReportCreateResponseDto createNewReport(Member member, MultipartFile imgFile, ReportCreateRequestDto dto) {
        SharingPost findPost = sharingPostRepository.findById(dto.getPostId())
                .orElseThrow(() -> new CustomException(
                        CustomExceptionStatus.NOT_FOUND_POST,
                        "해당 ID에 대응하는 SHARING POST가 존재하지 않습니다.",
                        this.getClass().getSimpleName(),
                        dto.getPostId(),
                        Domain.REPORT));

        if (findPost.getWriter().getId()==member.getId()){
            throw new CustomException(
                    CustomExceptionStatus.CANNOT_REPORT_SELF,
                    "본인의 게시글을 신고할 수 없습니다",
                    this.getClass().getSimpleName(),
                    "post writer id : "+findPost.getWriter().getId()+"\n login member id : "+member.getId(),
                    Domain.REPORT
                    );
        }

        String imgUrl = uploadReportImageToS3(imgFile);

        Report newReport = Report.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .status(ReportStatus.IN_PROGRESS)
                .response(null)
                .imgUrl(imgUrl)
                .reviewedAt(null)
                .responseAt(null)
                .reporter(member)
                .post(findPost)
                .build();
        Report savedReport = reportRepository.save(newReport);

        ReportMemberResponseComponent reporter = new ReportMemberResponseComponent(member.getId(), member.getNickname());

        ReportMemberResponseComponent writer= new ReportMemberResponseComponent(findPost.getWriter().getId(),findPost.getWriter().getNickname() );


        ReportPostResponseComponent post = new ReportPostResponseComponent(findPost.getId(), writer);

        return new ReportCreateResponseDto(
                savedReport.getId(),
                savedReport.getTitle(),
                savedReport.getContent(),
                savedReport.getImgUrl(),
                reporter,
                post,
                savedReport.getCreatedAt(),
                savedReport.getStatus().name()
        );
    }


    /* AWS S3에 신고 이미지 업로드 */
    private String uploadReportImageToS3(MultipartFile imgFile){
        String imgUrl="";

        if (imgFile==null){
            throw new CustomException(
                    CustomExceptionStatus.CANNOT_BE_NULL_IMG_FILE_FOR_REPORT,
                    "신고 시 사진 파일은 필수입니다",
                    this.getClass().getSimpleName(),
                    null,
                    Domain.REPORT);
        }else{
            String key="images/"+ UUID.randomUUID()+"_"+imgFile.getOriginalFilename();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(imgFile.getSize());
            metadata.setContentType(imgFile.getContentType());

            try (InputStream inputStream = imgFile.getInputStream()) {
                s3Client.putObject(bucketName, key, inputStream, metadata);
            } catch (IOException e) {
                throw new CustomException(
                        CustomExceptionStatus.AWS_S3_IMG_UPLOAD_CONNECTION_ERROR,
                        "AWS S3 이미지를 업로드 중 서버 내부의 에러가 발생하여 이미지를 S3에 업로드하지 못했습니다. \n Error message : "+e.getMessage(),
                        this.getClass().getSimpleName(),
                        null,
                        Domain.REPORT);
            }

            imgUrl = s3Client.getUrl(bucketName, key).toString();

        }
        return imgUrl;
    }
}
