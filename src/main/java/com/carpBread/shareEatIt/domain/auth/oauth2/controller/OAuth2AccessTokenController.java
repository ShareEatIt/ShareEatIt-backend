package com.carpBread.shareEatIt.domain.auth.oauth2.controller;

import com.carpBread.shareEatIt.domain.auth.LoginProvider;
import com.carpBread.shareEatIt.domain.member.service.module.MemberModuleService;
import com.carpBread.shareEatIt.global.exception.CustomException;
import com.carpBread.shareEatIt.global.exception.CustomExceptionStatus;
import com.carpBread.shareEatIt.global.exception.Domain;
import com.carpBread.shareEatIt.global.jwt.JWTUtils;
import com.carpBread.shareEatIt.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

// oauth2 로그인 진행 후 발급한 일회용 code로 accessToken 및 refreshToken 발급
@RestController
@RequestMapping("/oauth2/access-token")
@Slf4j
@RequiredArgsConstructor
public class OAuth2AccessTokenController {
    private final JWTUtils jwtUtils;
    private final RedisTemplate<String, Object> redisTemplate;
    private final MemberModuleService memberModuleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Boolean> createOAuth2LoginAccessToken(@RequestBody String code, HttpServletResponse response){
        System.out.println("OAuth2AccessTokenController.createOAuth2LoginAccessToken");
        System.out.println(code);

        // 1. 사용된 토큰인지 확인
        if (isUsedCode(code)){
            throw new CustomException(
                    CustomExceptionStatus.INVALID_JWT,
                    "이미 사용된 OAuth2Code 입니다.",
                    this.getClass().getSimpleName(),
                    null,
                    Domain.AUTH
            );
        }

        // 2. accessToken 및 refreshToken 발급
        String email = jwtUtils.getSubFromOAuth2Code(code);
        String provider = jwtUtils.getProviderFromOAuth2Code(code);
        Boolean isNewMem = jwtUtils.getIsNewMemFromOAuth2Code(code);

        String accessToken = "Bearer "+jwtUtils.createAccessToken(email, LoginProvider.toEnum(provider));
        String refreshToken = jwtUtils.createRefreshToken(email, LoginProvider.toEnum(provider));
        memberModuleService.updateRefreshTokenByEmail(email,refreshToken);

        // 3. header에 token을 넣는 방식
        response.setHeader("Authorization", accessToken);
        response.setHeader("RT-token", refreshToken);

        // 4. code 폐기
        String jti = jwtUtils.getJtiFromOAuth2Code(code);
        redisTemplate.opsForValue()
                .set(jti, code);

        // 5. response
        return new ApiResponse(
                HttpStatus.OK.value(),
                "OAuth2Code를 통해 AccessToken 발급이 완료되었습니다",
                isNewMem);

    }

    /* Redis에서 로그아웃된 토큰인지 파악 */
    private boolean isUsedCode(String code){
        // redis의 key 리스트
        String jti = jwtUtils.getJtiFromOAuth2Code(code);
        Set<String> keys = redisTemplate.keys(jti);

        // 해당 jti가 redis에 저장되어 있는 경우 로그아웃된 토큰이라고 파악
        if (!keys.isEmpty()){
            log.debug("이미 사용된 OAuth2Code 입니다.");
            return true;
        }
        return false;

    }
}
