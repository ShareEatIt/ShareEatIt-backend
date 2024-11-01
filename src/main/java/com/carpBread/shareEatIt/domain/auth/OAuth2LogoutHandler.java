package com.carpBread.shareEatIt.domain.auth;

import com.carpBread.shareEatIt.domain.auth.util.JWTUtils;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.repository.MemberRepository;
import com.carpBread.shareEatIt.global.exception.AppException;
import com.carpBread.shareEatIt.global.exception.ErrorCode;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Component
public class OAuth2LogoutHandler implements LogoutHandler {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    @Value("${kakao.api.logout-url}")
    private String kakaoLogoutUrl;

    @Value("${spring.oauth2.logout.direct-url}")
    private String logoutRedirectUri;

    private final WebClient webClient;
    private final MemberRepository memberRepository;
    private final JWTUtils jwtUtils;
    private final RedisTemplate<String,Object> redisTemplate;
    public OAuth2LogoutHandler(WebClient webClient, MemberRepository memberRepository, JWTUtils jwtUtils, RedisTemplate<String,Object> redisTemplate) {

        this.webClient=webClient;
        this.memberRepository=memberRepository;
        this.jwtUtils=jwtUtils;
        this.redisTemplate=redisTemplate;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        String token = null;
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);  // Extracts token after "Bearer "
        }else{
            throw new AppException(ErrorCode.UNAUTHORIZED_JWT,"유효하지 않은 인증 토큰입니다","/logout");
        }

        String email = jwtUtils.getEmail(token);
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_MEMBER, "해당 이메일에 맞는 회원 정보를 찾을 수 없습니다", "/logout"));
        String accessToken = member.getAccessToken();

        // redis에 jwt 저장 - 블랙리스트
        String redisKey = "token:"+email+":"+ UUID.randomUUID().toString();
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(redisKey,token);

        // kakao 서버에 '카카오 계정과 함께 로그아웃' 요청 보내기

        System.out.println(clientId+ logoutRedirectUri);

        if (accessToken != null) {
            webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(kakaoLogoutUrl)
                            .queryParam("client_id", clientId)
                            .queryParam("logout_redirect_uri", logoutRedirectUri)
                            .build())
                    .retrieve()
                    .bodyToMono(Void.class)
                    .doOnError(error -> {
                        // 로그아웃 중 오류가 발생한 경우 로깅
                        System.err.println("Error during Kakao logout: " + error.getMessage());
                    })
                    .subscribe();

            System.out.println("Kakao logout request sent for token: " + token);
        } else {
            throw new AppException(ErrorCode.LOGOUT_FAIL, "로그인 토큰이 존재하지 않습니다","/logout");
        }


    }
}
