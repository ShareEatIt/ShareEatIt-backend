package com.carpBread.shareEatIt.global.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UncheckedExceptionResponseDto {

    private final String exceptionStatus;
    private final String message;
    private final String cause;
    private final LocalDateTime timestamp;

    public UncheckedExceptionResponseDto(Exception e) {
        this.exceptionStatus = e.getClass().getSimpleName();  // 에러명
        this.message = e.getMessage();
        this.cause = String.valueOf(e.getCause());
        this.timestamp = LocalDateTime.now();
    }

    public UncheckedExceptionResponseDto(Exception e, String message) {
        this.exceptionStatus = e.getClass().getSimpleName();  // 에러명
        this.message = message;
        this.cause = String.valueOf(e.getCause());
        this.timestamp = LocalDateTime.now();
    }
}
