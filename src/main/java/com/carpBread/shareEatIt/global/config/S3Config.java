package com.carpBread.shareEatIt.global.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/* AWS S3 설정 */
@Configuration
public class S3Config {

    @Value("${cloud.aws.s3.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.s3.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.s3.region.static}")
    private String region;


    @Bean
    public AmazonS3 s3Client() {

        // aws 자격 증명 설정
        AWSCredentials credentials = new BasicAWSCredentials(accessKey,secretKey);
        AWSCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(credentials);

        return AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(awsCredentialsProvider)
                .build();
    }
}
