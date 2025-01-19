package com.carpBread.shareEatIt.domain.auth.oauth2.handler;

import com.carpBread.shareEatIt.domain.auth.LoginProvider;
import com.carpBread.shareEatIt.domain.auth.util.JWTUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;

/* oauth2 로그인 성공 시 클라이언트에게 token 전달(redirect) */
@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JWTUtils jwtUtils;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // 1. 인증 principal 받아오기
        OAuth2User principal = (OAuth2User) authentication.getPrincipal();

        // 2. 토큰 발행
        String email = (String) principal.getAttributes().get("email");
        LoginProvider provider = (LoginProvider) principal.getAttributes().get("provider");
        String accessToken = "Bearer "+jwtUtils.createAccessToken(email,provider);
        String refreshToken = "Bearer "+jwtUtils.createRefreshToken(email,provider);

        System.out.println(accessToken);

        // 3. 클라이언트 리다이렉트
        String redirectUrl = buildRedirectUrl(accessToken, refreshToken);


        System.out.println(request.getRequestURL());

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);


    }

    /* 프런트엔드 redirect url 빌드 */
    private String buildRedirectUrl(String accessToken, String refreshToken){
        return UriComponentsBuilder
                .fromUriString("http://localhost:3000/home")
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();
    }


}
