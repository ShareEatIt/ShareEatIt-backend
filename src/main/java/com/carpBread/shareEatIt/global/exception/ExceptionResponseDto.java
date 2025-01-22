package com.carpBread.shareEatIt.global.exception;

import jakarta.persistence.EntityListeners;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.swing.*;
import java.time.LocalDateTime;


@Getter
public class ExceptionResponseDto {

    // 오류 CustomExceptionStatus 값
    private String exceptionStatus;

    // 오류 메세지
    private String message;

    // 오류 발생 파일 경로
    private String filePath;

    // 문제가 되는 오류 REQUEST
    private String request;

    // 오류 발생 시점
    private LocalDateTime timestamp;

    // tag
    private String tag;

    public ExceptionResponseDto(CustomException e) {

        this.exceptionStatus = e.getExceptionStatus().name();
        this.message = e.getMessage();
        this.filePath = e.getFilePath();
        this.timestamp=e.getTimestamp();
        this.request = String.valueOf(e.getRequest());
        this.tag = e.getTag().name();
    }
}
