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
    NOT_FOUND_PARTICIPATION(HttpStatus.NOT_FOUND),
    ALREADY_COMPLETED_SHARINGPOST(HttpStatus.BAD_REQUEST),   // 이미 나눔완료된 나눔인 경우
    ALREADY_MATCHED_SHARINGPOST(HttpStatus.BAD_REQUEST),     // 이니 찜된 나눔인 경우
    ALREADY_AVAILABLE_SHARINGPSOT(HttpStatus.BAD_REQUEST),   // 이미 나눔가능한 상태의 나눔인 경우
    NOT_WRITER_OF_SHARINGPOST(HttpStatus.FORBIDDEN),         // 사용자가 나눔글의 writer인지 확인
    INVALID_STATUS_VALUE(HttpStatus.BAD_REQUEST),
    NOT_RECEIVER_OF_SHARINGPOST(HttpStatus.FORBIDDEN),        // 사용자가 나눔을 받은 참여자인지 확인
    MULTIPLE_COMPLETED_PARTICIPATIONS(HttpStatus.FORBIDDEN),  // 같은 나눔글에 대해 참여 상태가 COMPLETED인 참여 객체가 여러개인 경우

    // GratitudeSticker
    CAN_NOT_BE_NULL(HttpStatus.NO_CONTENT),                   // 값이 누락된 경우 예외 처리
    ALREADY_EXISTS_GRATITUDESTICKER(HttpStatus.FORBIDDEN),    // 이미 고마움을 남긴 경우
    NOT_FOUND_GRATITUDESTICKER(HttpStatus.NOT_FOUND),
    NOT_REVIEWER_OF_SHARINGPOST(HttpStatus.FORBIDDEN),

    // SharingPost
    NOT_FOUND_SHARINGPOST(HttpStatus.NOT_FOUND),
    NOT_COMPLETED_SHARINGPOST(HttpStatus.FORBIDDEN);

    private final HttpStatus status;
}
