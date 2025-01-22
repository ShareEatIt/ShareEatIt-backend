package com.carpBread.shareEatIt.global.exception;

import io.sentry.Sentry;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ExceptionResponseDto> handleAppException(HttpServletRequest request, CustomException e){
        // response dto 생성
        ExceptionResponseDto responseDto= new ExceptionResponseDto(e);

        // Sentry 시스템에 전송
        Sentry.configureScope(scope ->{
            scope.setContexts("file_path", responseDto.getFilePath());
            scope.setContexts("exception_status", responseDto.getExceptionStatus());
            scope.setContexts("message",responseDto.getMessage());
            scope.setContexts("timestamp", responseDto.getTimestamp());
            scope.setContexts("request", responseDto.getRequest());
            scope.setTag("tag", responseDto.getTag());
        });

        return ResponseEntity.status(e.getExceptionStatus().getStatus()).body(responseDto);
    }
}
