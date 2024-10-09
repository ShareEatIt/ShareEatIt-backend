package com.carpBread.shareEatIt.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponseDto> handleAppException(AppException e){

        System.out.println("GlobalExceptionHandler.handleAppException");
        ErrorResponseDto responseDto=ErrorResponseDto.builder()
                .status(e.getErrorCode().getStatus().value())
                .message(e.getMessage())
                .path(e.getPath())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(e.getErrorCode().getStatus()).body(responseDto);
    }
}
