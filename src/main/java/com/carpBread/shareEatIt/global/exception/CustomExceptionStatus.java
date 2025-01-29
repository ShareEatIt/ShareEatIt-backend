package com.carpBread.shareEatIt.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CustomExceptionStatus {

    // SignUp
    ALREADY_EXISTS_USERNAME(HttpStatus.CONFLICT),
    ALREADY_EXISTS_EMAIL(HttpStatus.CONFLICT),

    // Member
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND),
    INVALID_ACCESS_TOKEN(HttpStatus.NOT_ACCEPTABLE),
    INVALID_S3_URL(HttpStatus.NOT_ACCEPTABLE),

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

    // point - longitude, latitude
    VALUE_OUT_OF_RANGE(HttpStatus.BAD_REQUEST),

    // SharingPost
    NOT_FOUND_SHARINGPOST(HttpStatus.NOT_FOUND),
    NOT_COMPLETED_SHARINGPOST(HttpStatus.FORBIDDEN),
    AWS_S3_IMG_UPLOAD_CONNECTION_ERROR(HttpStatus.CONFLICT),
    INVALID_ENUM_VALUE(HttpStatus.NOT_FOUND),
    INVALID_PROVIDER_WITH_POSTTYPE_STORE(HttpStatus.UNAUTHORIZED),
    NOT_FOUND_POST_IMAGE(HttpStatus.NOT_FOUND),
    NOT_FOUND_POST(HttpStatus.NOT_FOUND),
    UNAUTHORIZED_MEMBER_TO_UPDATE_POST(HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED_MEMBER_TO_DELETE_POST(HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED_UPDATE_POST(HttpStatus.UNAUTHORIZED),

    // auth
    LOGOUT_FAIL(HttpStatus.CONFLICT),
    UNAUTHORIZED_JWT(HttpStatus.UNAUTHORIZED),
    INVALID_JWT(HttpStatus.BAD_REQUEST),
    LOGIN_FAIL(HttpStatus.CONFLICT),
    NOT_FOUND_OAUTH2_REGISTRATION_ID(HttpStatus.NOT_FOUND),
    INVALID_REFRESH_TOKEN(HttpStatus.FORBIDDEN),
    INVALID_LOGIN_TYPE(HttpStatus.NOT_FOUND),
    NOT_FOUND_OAUTH2_ACCESS_TOKEN(HttpStatus.NOT_FOUND),

    // Keyword
    ALREADY_USING_KEYWORD(HttpStatus.IM_USED),
    NOT_FOUND_KEYWORD_UNAVAILABLE_ID(HttpStatus.NOT_FOUND),
    NOT_AVAILABLE_MEMBER_TO_DELETE_KEYWORD(HttpStatus.FORBIDDEN),

    // notice
    NOTICE_SEND_FAIL(HttpStatus.CONFLICT),
    NOT_FOUND_NOTICE(HttpStatus.NOT_FOUND),
    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED),
    ALREADY_READ(HttpStatus.FORBIDDEN),

    // report
    CANNOT_REPORT_SELF(HttpStatus.FORBIDDEN),
    CANNOT_BE_NULL_IMG_FILE_FOR_REPORT(HttpStatus.NO_CONTENT),
    ALREADY_EXISTS_REPORT(HttpStatus.ALREADY_REPORTED),

    // ChatRoom
    NOT_FOUND_CHATROOM(HttpStatus.NOT_FOUND),
    CAN_NOT_PARTICIPATE_MY_POST(HttpStatus.FORBIDDEN),
    NOT_FOUND_OPPONENT(HttpStatus.NOT_FOUND),

    // Chat
    NOT_MEMBER_OF_CHATROOM(HttpStatus.FORBIDDEN);

    private final HttpStatus status;
}
