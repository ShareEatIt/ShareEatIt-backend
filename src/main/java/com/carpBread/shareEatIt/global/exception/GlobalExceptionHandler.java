package com.carpBread.shareEatIt.global.exception;

import io.sentry.Sentry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponseDto> handleAppException(AppException e){
        // sentry 시스템에 전송
        Sentry.configureScope(scope ->{
            scope.setContexts("file location", "MemberService.java");
            scope.setContexts("error enum", e.getErrorCode());
            scope.setContexts("error occur field","name");
            scope.setTag("tier", "service");
        });

        ErrorResponseDto responseDto=ErrorResponseDto.builder()
                .status(e.getErrorCode().getStatus().value())
                .message(e.getMessage())
                .path(e.getPath())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(e.getErrorCode().getStatus()).body(responseDto);
    }
}
