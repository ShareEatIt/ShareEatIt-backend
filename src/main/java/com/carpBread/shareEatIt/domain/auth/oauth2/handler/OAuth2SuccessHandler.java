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

        // 2. 토큰 발행
        String email = (String) principal.getAttributes().get("email");
        Boolean isNewMember = (Boolean) principal.getAttributes().get("is_new_member");
        LoginProvider provider = (LoginProvider) principal.getAttributes().get("provider");
//        String accessToken = "Bearer "+jwtUtils.createAccessToken(email,provider);
        String accessToken = jwtUtils.createAccessToken(email,provider);
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> null);
        String refreshToken = member.getRefreshToken();

        System.out.println(accessToken);

        // 3. 클라이언트 리다이렉트
        AuthLoginResponseDto responseDto = new AuthLoginResponseDto(accessToken, refreshToken, isNewMember);

        String redirectUrl = buildRedirectUrl(responseDto);

        // cookie 생성
        Cookie cookie1 = new Cookie("AccessToken", accessToken);
        cookie1.setHttpOnly(true);
        cookie1.setSecure(true);
        cookie1.setPath("/");
        cookie1.setMaxAge(60*60*24);
        response.addCookie(cookie1);

        Cookie cookie2 = new Cookie("RefreshToken", refreshToken);
        cookie2.setHttpOnly(true);
        cookie2.setSecure(true);
        cookie2.setPath("/");
        cookie2.setMaxAge(60*60*24*30);
        response.addCookie(cookie2);

        Cookie cookie3 = new Cookie("isNewMember", isNewMember.toString());
        cookie3.setHttpOnly(true);
        cookie3.setSecure(true);
        cookie3.setPath("/");
        cookie3.setMaxAge(60*60*24);
        response.addCookie(cookie3);

        response.sendRedirect(clientRedirectUrl);

        System.out.println("OAuth2SuccessHandler.onAuthenticationSuccess : 리다이랙트 처리 완료");


        // 보안 관련 테스트 위한 주석처리
//        getRedirectStrategy().sendRedirect(request, response, redirectUrl);


    }

    /* 프런트엔드 redirect url 빌드 */
    private String buildRedirectUrl(AuthLoginResponseDto dto){
        return UriComponentsBuilder
                .fromUriString(clientRedirectUrl)
                .queryParam("accessToken", dto.getAccessToken())
                .queryParam("refreshToken", dto.getRefreshToken())
                .queryParam("isNewMember", dto.getIsNewMember())
                .build().toUriString();
    }


}
