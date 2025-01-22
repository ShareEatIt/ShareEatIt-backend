package com.carpBread.shareEatIt.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException{

    private CustomExceptionStatus customExceptionStatus;
    private String message;
    private String path;
}
