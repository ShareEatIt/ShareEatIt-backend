package com.carpBread.shareEatIt.global.exception;

import jakarta.persistence.EntityListeners;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/* 어플리케이션 내에서 정의하는 CustomException */
@Getter
@EntityListeners(AuditingEntityListener.class)
public class CustomException extends RuntimeException{

    // 오류 CustomExceptionStatus 값
    private CustomExceptionStatus exceptionStatus;

    // 오류 메세지
    private String message;

    // 오류 발생 파일 경로
    private String filePath;

    // 문제가 되는 오류 REQUEST
    private Object request;

    // 오류 발생 시점
    private final LocalDateTime timestamp=LocalDateTime.now();

    // tag
    private Domain tag;

    public CustomException(CustomExceptionStatus exceptionStatus,String message,
                           String filePath, Object request, Domain tag){
        this.exceptionStatus=exceptionStatus;
        this.message=message;
        this.filePath=filePath;
        this.request=request;
        this.tag=tag;
    }

}
