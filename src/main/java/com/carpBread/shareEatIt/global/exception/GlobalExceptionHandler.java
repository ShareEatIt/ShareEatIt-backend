package com.carpBread.shareEatIt.global.exception;

import io.sentry.Sentry;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
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
    public ResponseEntity<CustomExceptionResponseDto> handleAppException(CustomException e){
        // response dto 생성
        CustomExceptionResponseDto responseDto= new CustomExceptionResponseDto(e);

        // Sentry 시스템에 전송

        Sentry.configureScope(scope ->{
            scope.setContexts("file_path", responseDto.getFilePath());
            scope.setContexts("exception_status", responseDto.getExceptionStatus());
            scope.setContexts("message",responseDto.getMessage());
            scope.setContexts("timestamp", responseDto.getTimestamp());
            scope.setContexts("causation", responseDto.getCausation());
            scope.setTag("tag", responseDto.getTag());
        });

        // controller에서와 달리 exception 발생 시에는 각 HttpStatus가 다르므로, status() 함수 사용을 위해 ResponseEntity를 사용하였습니다.
        return ResponseEntity.status(e.getExceptionStatus().getStatus()).body(responseDto);
    }
    
}
