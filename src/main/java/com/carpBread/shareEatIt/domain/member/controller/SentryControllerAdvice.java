package com.carpBread.shareEatIt.domain.member.controller;

import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class SentryControllerAdvice {

    @ExceptionHandler(RuntimeException.class)
    protected ResponseEntity<?> handleRuntimeException(RuntimeException e){
        Sentry.captureException(e);
        return ResponseEntity
                .badRequest()
                .body(e.getMessage());
    }
}
