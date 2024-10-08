package com.carpBread.shareEatIt.global.response;

import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class ApiResponse<T> {

    private int status;
    private LocalDateTime timestamp;
    private String message;
    private T data;

    public ApiResponse(int status, String message, T data) {
        this.status = status;
        this.timestamp = LocalDateTime.now();
        this.message = message;
        this.data = data;
    }
}