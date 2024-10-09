package com.carpBread.shareEatIt.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND),
    INVALID_ACCESS_TOKEN(HttpStatus.NOT_ACCEPTABLE);

    private final HttpStatus status;
}
