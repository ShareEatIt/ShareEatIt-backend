package com.carpBread.shareEatIt.global.exception;

import lombok.Getter;

import java.time.LocalDateTime;

// ExceptionHandler 내부에서만 사용하는 responseDto
@Getter
public class CustomExceptionResponseDto {

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

    public CustomExceptionResponseDto(CustomException e) {

        this.exceptionStatus = e.getExceptionStatus().name();
        this.message = e.getMessage();
        this.filePath = e.getFilePath();
        this.timestamp=e.getTimestamp();
        this.request = String.valueOf(e.getRequest());
        this.tag = e.getTag().name();
    }
}
