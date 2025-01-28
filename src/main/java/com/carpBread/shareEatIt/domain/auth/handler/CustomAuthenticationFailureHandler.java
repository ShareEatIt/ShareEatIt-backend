package com.carpBread.shareEatIt.domain.auth.handler;

import com.carpBread.shareEatIt.global.exception.CustomException;
import com.carpBread.shareEatIt.global.exception.CustomExceptionResponseDto;
import com.carpBread.shareEatIt.global.exception.CustomExceptionStatus;
import com.carpBread.shareEatIt.global.exception.Domain;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sentry.Sentry;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/* 자체 로그인 시 인증 과정에서 오류가 발생했을 경우 오류 메세지 전송하는 failure handler */
@Component
@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        CustomException customException = new CustomException(
                CustomExceptionStatus.LOGIN_FAIL,
                "어플리케이션 자체 로그인 인증과정에서 오류가 발생하여 로그인에 실패했습니다. \n Error message : "+exception.getMessage(),
                CustomAuthenticationFailureHandler.class.getName(),
                null,
                Domain.AUTH);

        CustomExceptionResponseDto responseDto = new CustomExceptionResponseDto(customException);

        // Sentry 시스템에 전송

        Sentry.configureScope(scope ->{
            scope.setContexts("file_path", responseDto.getFilePath());
            scope.setContexts("exception_status", responseDto.getExceptionStatus());
            scope.setContexts("message",responseDto.getMessage());
            scope.setContexts("timestamp", responseDto.getTimestamp());
            scope.setContexts("causation", responseDto.getCausation());
            scope.setTag("tag", responseDto.getTag());
        });

        // 클라이언트에 JSON 응답 전달
        response.setStatus(customException.getExceptionStatus().getStatus().value()); // HTTP 상태 설정
        response.setContentType("application/json"); // Content-Type 설정
        response.setCharacterEncoding("UTF-8"); // Encoding 설정

        // ObjectMapper를 사용해 DTO를 JSON 문자열로 변환
        String jsonResponse = objectMapper.writeValueAsString(responseDto);

        // 응답 본문 작성
        response.getWriter().write(jsonResponse);

    }
}
