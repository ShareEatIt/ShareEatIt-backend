package com.carpBread.shareEatIt.domain.auth.oauth2.handler;

import com.carpBread.shareEatIt.domain.auth.dto.response.AuthLoginResponseDto;
import com.carpBread.shareEatIt.domain.auth.LoginProvider;
import com.carpBread.shareEatIt.global.jwt.JWTUtils;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.repository.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

/* oauth2 로그인 성공 시 클라이언트에게 token 전달(redirect) */
@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JWTUtils jwtUtils;
    private final MemberRepository memberRepository;

    @Value("${spring.security.oauth2.client-redirect-url}")
    private String clientRedirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // 1. 인증 principal 받아오기
        OAuth2User principal = (OAuth2User) authentication.getPrincipal();

        // 2. 정보 추출
        String email = (String) principal.getAttributes().get("email");
        Boolean isNewMember = (Boolean) principal.getAttributes().get("is_new_member");
        LoginProvider provider = (LoginProvider) principal.getAttributes().get("provider");

        // 3. 코드 발급
        String oAuth2Code = jwtUtils.createOAuth2Code(email, provider, isNewMember);

        // 4. redirect url
        String redirectUrl = UriComponentsBuilder.fromUriString(clientRedirectUrl)
                .queryParam("code", oAuth2Code)
                .build().encode().toString();

        String accessToken = jwtUtils.createAccessToken(email, provider);
        ResponseCookie responseCookie = ResponseCookie.from("sAccessToken", accessToken)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .maxAge(60 * 60 * 3)
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

        // redirect
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);


    }


}
