package com.carpBread.shareEatIt.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND),
    INVALID_ACCESS_TOKEN(HttpStatus.NOT_ACCEPTABLE),

    // Participation
    NOT_FOUND_SHARINGPOST(HttpStatus.NOT_FOUND),
    NOT_FOUND_PARTICIPATION(HttpStatus.NOT_FOUND),
    ALREADY_COMPLETED_SHARINGPOST(HttpStatus.BAD_REQUEST),   // 이미 나눔완료된 나눔인 경우
    ALREADY_MATCHED_SHARINGPOST(HttpStatus.BAD_REQUEST),     // 이니 찜된 나눔인 경우
    ALREADY_AVAILABLE_SHARINGPSOT(HttpStatus.BAD_REQUEST),   // 이미 나눔가능한 상태의 나눔인 경우
    NOT_WRITER_OF_SHARINGPOST(HttpStatus.FORBIDDEN),         // 사용자가 나눔긓의 writer인지 확인
    INVALID_STATUS_VALUE(HttpStatus.BAD_REQUEST);

    private final HttpStatus status;
}
