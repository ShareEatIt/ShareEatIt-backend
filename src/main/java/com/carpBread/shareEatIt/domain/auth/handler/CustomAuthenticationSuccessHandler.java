package com.carpBread.shareEatIt.domain.auth.handler;

import com.carpBread.shareEatIt.domain.auth.dto.response.AuthLoginResponseDto;
import com.carpBread.shareEatIt.domain.auth.LoginProvider;
import com.carpBread.shareEatIt.domain.auth.dto.CustomUserDetails;
import com.carpBread.shareEatIt.global.jwt.JWTUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/* 자체 로그인이 성공할 경우 프런트엔드에 token 발급하는 successHandler */
@Component
@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JWTUtils jwtUtils;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

        // 사용자의 username으로 JWT 생성
        String username = principal.getUsername();
        String accessToken = "Bearer "+jwtUtils.createAccessToken(username, LoginProvider.LOCAL);
        String refreshToken = jwtUtils.createRefreshToken(username, LoginProvider.LOCAL);

        // http response
        AuthLoginResponseDto responseDto= new AuthLoginResponseDto(
                accessToken,
                refreshToken,
                false);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(responseDto));

        log.debug(accessToken);
    }

}
