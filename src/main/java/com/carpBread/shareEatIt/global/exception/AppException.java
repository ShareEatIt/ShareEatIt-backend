package com.carpBread.shareEatIt.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AppException extends RuntimeException{

    private ErrorCode errorCode;
    private String message;
    private String path;
}
