package com.carpBread.shareEatIt.global.exception;

import io.sentry.Sentry;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;

/* 어플리케이션 내 발생하는 런타임 예외 핸들러 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /* CustomException 핸들러 */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CustomExceptionResponseDto> handleAppException(CustomException e, HttpServletRequest request){
        // response dto 생성
        CustomExceptionResponseDto responseDto= new CustomExceptionResponseDto(e);

        // Sentry 시스템에 전송
        Sentry.init(options -> {
            options.setDsn("https://1c2ac0504036a173f428c1d39c271693@o4508679774142464.ingest.us.sentry.io/4508679775584256");
        });

        Sentry.configureScope(scope ->{
            scope.setTransaction(request.getRequestURI());
            scope.setExtra("file_path", responseDto.getFilePath());
            scope.setExtra("exception_status", responseDto.getExceptionStatus());
            scope.setExtra("message",responseDto.getMessage());
            scope.setExtra("timestamp", String.valueOf(responseDto.getTimestamp()));
            scope.setExtra("causation", responseDto.getCausation());
            scope.setExtra("request_method", request.getMethod());
            scope.setExtra("request_uri", request.getRequestURI());
            scope.setTag("tag", responseDto.getTag());
            Sentry.captureException(e);
        });

        // controller에서와 달리 exception 발생 시에는 각 HttpStatus가 다르므로, status() 함수 사용을 위해 ResponseEntity를 사용하였습니다.
        return ResponseEntity.status(e.getExceptionStatus().getStatus()).body(responseDto);
    }
    
}
