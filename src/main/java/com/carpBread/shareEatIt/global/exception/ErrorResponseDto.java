package com.carpBread.shareEatIt.global.exception;

import jakarta.persistence.EntityListeners;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


@AllArgsConstructor
@Getter @Builder
@EntityListeners(AuditingEntityListener.class)
public class ErrorResponseDto {

    private LocalDateTime timestamp;
    private int status;
    private final String error = "server connection error";
    private String message;
    private String path;


}
