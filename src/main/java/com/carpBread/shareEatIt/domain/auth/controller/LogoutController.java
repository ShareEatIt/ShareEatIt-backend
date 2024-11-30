package com.carpBread.shareEatIt.domain.auth.controller;

import com.carpBread.shareEatIt.domain.auth.AuthLoginResponseDto;
import com.carpBread.shareEatIt.domain.auth.AuthUser;
import com.carpBread.shareEatIt.domain.auth.dto.RefreshRequestDto;
import com.carpBread.shareEatIt.domain.auth.dto.RefreshTokenResponseDto;
import com.carpBread.shareEatIt.domain.auth.util.JWTUtils;
import com.carpBread.shareEatIt.domain.member.dto.LogoutResponseDto;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.repository.MemberRepository;
import com.carpBread.shareEatIt.global.exception.AppException;
import com.carpBread.shareEatIt.global.exception.ErrorCode;
import com.carpBread.shareEatIt.global.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class LogoutController {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final MemberRepository memberRepository;
    private final JWTUtils jwtUtils;
    private final RedisTemplate<String,Object> redisTemplate;
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    String clientId;

    @Value("${kakao.api.logout-url}")
    String kakaoLogoutUrl;

    @Value("${spring.oauth2.logout.direct-url}")
    String logoutRedirectUri;

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RefreshTokenResponseDto>> refreshAccessToken(@RequestBody @Valid RefreshRequestDto dto)throws Exception{

        Member member = memberRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_MEMBER, "해당 이메일에 맞는 회원 정보를 찾을 수 없습니다", "/logout"));

        if(!dto.getRefreshToken().equals(member.getRefreshToken()))
            throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN,"유효하지 않은 리프레시 토큰입니다. 재로그인해주십시오","/auth/refresh");

        String newAccessToken = jwtUtils.createToken(member.getEmail(), member.getNickname(), 1000 * 60 * 60 * 12);
        String newRefreshToken = jwtUtils.createToken(member.getEmail(), member.getNickname(), 1000 * 60 * 60 * 24);
        member.updateRefreshToken(newRefreshToken);
        memberRepository.save(member);

        System.out.println(newAccessToken);
        System.out.println(newRefreshToken);
        System.out.println("LogoutController.refreshAccessToken");

        RefreshTokenResponseDto refreshTokenResponseDto = RefreshTokenResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();

        ApiResponse<RefreshTokenResponseDto> responseDto = new ApiResponse<>(HttpStatus.OK.value(), "리프레시 토큰 발급 성공", refreshTokenResponseDto);

        return ResponseEntity.ok().body(responseDto);

    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request){
        String token = null;
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer")) {
            token = authHeader.split(" ")[1];
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
