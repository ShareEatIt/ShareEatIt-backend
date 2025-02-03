package com.carpBread.shareEatIt.domain.auth.oauth2.handler;

import com.carpBread.shareEatIt.domain.auth.handler.CustomAuthenticationFailureHandler;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

/* oauth2 로그인 실패 시 클라이언트에게 실패 메세지 전달 */
@Component
@Slf4j
@RequiredArgsConstructor
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Value("${sentry.dsn}")
    private String dsn;

    private final ObjectMapper objectMapper;
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        CustomException customException = new CustomException(
                CustomExceptionStatus.LOGIN_FAIL,
                "OAUTH2 로그인 인증과정에서 오류가 발생하여 로그인에 실패했습니다. "+
                System.lineSeparator()+" Error message : "+exception.getMessage(),
                this.getClass().getSimpleName(),
                null,
                Domain.AUTH);

        CustomExceptionResponseDto responseDto = new CustomExceptionResponseDto(customException);

        // Sentry 시스템에 전송
        Sentry.init(options -> {
            options.setDsn(
                    dsn
            );
        });

        Sentry.configureScope(scope ->{
            scope.setTransaction(request.getRequestURI());
            scope.setExtra("file_path", responseDto.getFilePath());
            scope.setExtra("exception_status", responseDto.getExceptionStatus());
            scope.setExtra("message",responseDto.getMessage());
            scope.setExtra("timestamp", String.valueOf(responseDto.getTimestamp()));
            scope.setExtra("causation", responseDto.getCausation());
            scope.setExtra("request_method", request.getMethod());
            scope.setExtra("request_uri", request.getRequestURI());
            scope.setTag("tag", responseDto.getTag());
            Sentry.captureException(customException);
        });

        // 클라이언트에 JSON 응답 전달
        response.setStatus(customException.getExceptionStatus().getStatus().value()); // HTTP 상태 설정
        response.setContentType("application/json"); // Content-Type 설정
        response.setCharacterEncoding("UTF-8"); // Encoding 설정

        // ObjectMapper를 사용해 DTO를 JSON 문자열로 변환
        String jsonResponse = objectMapper.writeValueAsString(responseDto);

        // 응답 본문 작성
        response.getWriter().write(jsonResponse);

        // 클라리언트 리다이랙트
        String redirectUrl = buildRedirectUrl();

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);

    }

    /* 프런트엔드 redirect url 빌드 */
    private String buildRedirectUrl(){
        return UriComponentsBuilder
                .fromUriString("http://localhost:3000/home")
                .build().toUriString();
    }
}
