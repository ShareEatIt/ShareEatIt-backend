package com.carpBread.shareEatIt.global.jwt;

import com.carpBread.shareEatIt.global.exception.CustomException;
import com.carpBread.shareEatIt.global.exception.CustomExceptionResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sentry.Sentry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/* JWT 인증 시 발생하는 CustomException handler */
@Slf4j
@RequiredArgsConstructor
public class JWTCustomExceptionHandler extends OncePerRequestFilter {
    private final ObjectMapper objectMapper;

    @Value("${sentry.dsn}")
    private String dsn;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            // JWTCustomExceptionHandler 를 JWTFilter보다 이전에 두었기 때문에, doFilter 함수를 통해 JWTFilter를 수행시키고,
            // 수행중에 발생하는 CustomException을 JWTCustomExceptionHandler에서 잡을 수 있습니다.
            filterChain.doFilter(request,response);
        }catch (CustomException e){
            // response dto 생성
            CustomExceptionResponseDto responseDto= new CustomExceptionResponseDto(e);

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
            response.setStatus(e.getExceptionStatus().getStatus().value()); // HTTP 상태 설정
            response.setContentType("application/json"); // Content-Type 설정
            response.setCharacterEncoding("UTF-8"); // Encoding 설정

            // ObjectMapper를 사용해 DTO를 JSON 문자열로 변환
            String jsonResponse = objectMapper.writeValueAsString(responseDto);

            // 응답 본문 작성
            response.getWriter().write(jsonResponse);


        }

    }
}
