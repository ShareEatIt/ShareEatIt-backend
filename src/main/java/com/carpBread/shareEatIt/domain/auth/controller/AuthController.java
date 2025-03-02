package com.carpBread.shareEatIt.domain.auth.controller;

import com.carpBread.shareEatIt.domain.auth.LoginProvider;
import com.carpBread.shareEatIt.domain.auth.dto.request.RefreshRequestDto;
import com.carpBread.shareEatIt.domain.auth.dto.response.RefreshTokenResponseDto;
import com.carpBread.shareEatIt.domain.member.service.module.MemberModuleService;
import com.carpBread.shareEatIt.global.jwt.JWTUtils;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.repository.MemberRepository;
import com.carpBread.shareEatIt.global.exception.CustomException;
import com.carpBread.shareEatIt.global.exception.CustomExceptionStatus;
import com.carpBread.shareEatIt.global.exception.Domain;
import com.carpBread.shareEatIt.global.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Set;

/* 로그아웃, 리프레시 토큰 관련 인증 핸들러 */
// 💡 참고 : 로그인/로그아웃 관련하여 변경사항이 많아 수정된 내용이 많아 코드만 남겨둔 상태. 프런트 연결 후 삭제 예정임.
@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {

    private final MemberModuleService memberModuleService;
    private final JWTUtils jwtUtils;
    private final RedisTemplate<String, Object> redisTemplate;
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    String clientId;

    @Value("${kakao.api.logout-url}")
    String kakaoLogoutUrl;

    @Value("${spring.oauth2.logout.direct-url}")
    String logoutRedirectUri;

    /* 리프레시 토큰 발급, 수정 예정!!! */
    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<String> refreshAccessToken(@RequestBody String refreshToken, HttpServletResponse response) throws Exception{

        System.out.println("AuthController.refreshAccessToken");


        // 폐기된 refreshToken인지 확인
        String jti = jwtUtils.getJtiFromRefreshToken(refreshToken);
        Set<String> keys = redisTemplate.keys(jti);

        // 해당 jti가 redis에 저장되어 있는 경우 로그아웃된 토큰이라고 파악
        if (!keys.isEmpty()){
            throw new CustomException(CustomExceptionStatus.UNAUTHORIZED_JWT,
                    "이미 사용된 Refresh Token입니다. 다시 로그인해주십시오.",
                    this.getClass().getSimpleName(),
                    null,
                    Domain.AUTH);
        }

        System.out.println("AuthController.refreshAccessToken1");

        String sub = jwtUtils.getSubFromRefreshToken(refreshToken);
        String provider = jwtUtils.getProviderFromRefreshToken(refreshToken);

        System.out.println("AuthController.refreshAccessToken2");

        // refreshToken 폐기
        redisTemplate.opsForValue()
                .set(jti, refreshToken);


        // 새 토큰 발급
        String newAccessToken = "Bearer "+jwtUtils.createAccessToken(sub, LoginProvider.toEnum(provider));
        String newRefreshToken = jwtUtils.createRefreshToken(sub, LoginProvider.toEnum(provider));

        System.out.println("AuthController.refreshAccessToken3");

        // db refreshToken 갱신
        if (provider.equals(LoginProvider.LOCAL.name())){
            memberModuleService.updateRefreshTokenByUsername(sub,newRefreshToken);
        }else {
            memberModuleService.updateRefreshTokenByEmail(sub, newRefreshToken);
        }

        // header에 전달
        response.setHeader("Authorization", newAccessToken);
        response.setHeader("RT-token", newRefreshToken);

        System.out.println(newAccessToken);
        System.out.println(newRefreshToken);

        // response
        return new ApiResponse<>(HttpStatus.CREATED.value(), "refresh token 을 사용한 access token 발급 성공", "Access token successfully issued by Refresh Token");

    }


}
