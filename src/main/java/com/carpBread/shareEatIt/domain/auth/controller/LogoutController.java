package com.carpBread.shareEatIt.domain.auth.controller;

import com.carpBread.shareEatIt.domain.auth.AuthUser;
import com.carpBread.shareEatIt.domain.auth.util.JWTUtils;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.repository.MemberRepository;
import com.carpBread.shareEatIt.global.exception.AppException;
import com.carpBread.shareEatIt.global.exception.ErrorCode;
import com.carpBread.shareEatIt.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class LogoutController {

    private final WebClient webClient;
    private final MemberRepository memberRepository;
    private final JWTUtils jwtUtils;
    private final RedisTemplate<String,Object> redisTemplate;
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    String clientId;

    @Value("${kakao.api.logout-url}")
    String kakaoLogoutUrl;

    @Value("${spring.oauth2.logout.direct-url}")
    String logoutRedirectUri;

    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request){
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

        if (accessToken != null) {
            webClient.get()
                    .uri(kakaoLogoutUrl + "?client_id=" + clientId + "&logout_redirect_uri=" + logoutRedirectUri)
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


        return ResponseEntity.ok().body("로그아웃이 성공적으로 완료되었습니다");
    }

}
