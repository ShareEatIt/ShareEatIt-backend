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
                .orElseThrow(() -> new CustomException(CustomExceptionStatus.NOT_FOUND_POST, "해당 id에 대응하는 SHARING POST가 존재하지 않습니다.", "/report"));

        if (findPost.getWriter().getId()==member.getId()){
            throw new CustomException(CustomExceptionStatus.CANNOT_REPORT_SELF,"본인의 게시글을 신고할 수 없습니다","/report");
        }

        String imgUrl="";

        if (imgFile==null){
            throw new CustomException(CustomExceptionStatus.CANNOT_BE_NULL_IMG_FILE_FOR_REPORT,"신고 시 사진 파일은 필수입니다","/report");
        }else{
            String key="images/"+ UUID.randomUUID()+"_"+imgFile.getOriginalFilename();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(imgFile.getSize());
            metadata.setContentType(imgFile.getContentType());

            try (InputStream inputStream = imgFile.getInputStream()) {
                s3Client.putObject(bucketName, key, inputStream, metadata);
            } catch (IOException e) {
                throw new CustomException(CustomExceptionStatus.AWS_S3_IMG_UPLOAD_CONNECTION_ERROR, "sharing post create - POST error", "/sharing");
            }

            imgUrl = s3Client.getUrl(bucketName, key).toString();

        }

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

        ReportMemberResponseComponent reporter = ReportMemberResponseComponent.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .build();
        ReportMemberResponseComponent writer= ReportMemberResponseComponent.builder()
                .id(findPost.getWriter().getId())
                .nickname(findPost.getWriter().getNickname())
                .build();

        ReportPostResponseComponent post = ReportPostResponseComponent.builder()
                .id(findPost.getId())
                .writer(writer)
                .build();

        return ReportCreateResponseDto.builder()
                .id(savedReport.getId())
                .title(savedReport.getTitle())
                .content(savedReport.getContent())
                .imgUrl(savedReport.getImgUrl())
                .createdAt(savedReport.getCreatedAt())
                .status(savedReport.getStatus().name())
                .reporter(reporter)
                .post(post)
                .build();

    }
}
