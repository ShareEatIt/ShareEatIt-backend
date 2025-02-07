package com.carpBread.shareEatIt.global.service.module;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.carpBread.shareEatIt.domain.sharingPost.entity.PostImgUrl;
import com.carpBread.shareEatIt.domain.sharingPost.repository.PostImgUrlRepository;
import com.carpBread.shareEatIt.global.exception.CustomException;
import com.carpBread.shareEatIt.global.exception.CustomExceptionStatus;
import com.carpBread.shareEatIt.global.exception.Domain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/* S3에 이미지를 업로드하는 Module Service */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class S3ImageUploadService {
    private final AmazonS3 s3Client;
    private final PostImgUrlRepository postImgUrlRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    // 이미지 1개 업로드 및 url 반환
    public String uploadOneImageToS3Bucket(MultipartFile imgFile){
        // key 지정
        String key = "images/" + UUID.randomUUID() + "_" + imgFile.getOriginalFilename();

        // 메타데이터 생성
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(imgFile.getSize());
        metadata.setContentType(imgFile.getContentType());

        // stream으로 저장
        try(InputStream inputStream = imgFile.getInputStream()){
            s3Client.putObject(bucketName,key,inputStream, metadata);

        }catch (IOException e){
            throw new CustomException(
                    CustomExceptionStatus.AWS_S3_IMG_UPLOAD_CONNECTION_ERROR,
                    "AWS S3에 이미지를 업로드하는 과정에 오류가 발생하여 S3에 이미지를 업로드하지 못했습니다. \n Error message : "+e.getMessage(),
                    this.getClass().getSimpleName(),
                    null,
                    Domain.SHARING_POST
            );
        }

        return s3Client.getUrl(bucketName,key).toString();

    }


}
