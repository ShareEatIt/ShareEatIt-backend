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
    public ApiResponse<Boolean> createOAuth2LoginAccessToken(@RequestBody String code, HttpServletResponse response){

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

        String accessToken = jwtUtils.createAccessToken(email, LoginProvider.toEnum(provider));
        String refreshToken = jwtUtils.createRefreshToken(email, LoginProvider.toEnum(provider));
        memberModuleService.updateRefreshToken(email,refreshToken);

        // 3. 쿠키 생성
        ResponseCookie accessTokenCookie = ResponseCookie.from("AccessToken", accessToken)
                .maxAge(60 * 60 * 3) // 3시간
                .secure(true) // https 안에서만 유효
                .sameSite("None") // same site 설정 무효
                .httpOnly(true) // js로 읽어들일 수 없음
                .path("/") // 이 경로로 시작되는 모든 경로에서 사용 가능
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());

        ResponseCookie refreshTokenCookie = ResponseCookie.from("RefreshToken", refreshToken)
                .maxAge(60 * 60 * 24 * 30) // 30일
                .secure(true) // https 안에서만 유효
                .sameSite("None") // same site 설정 무효
                .httpOnly(true) // js로 읽어들일 수 없음
                .path("/") // 이 경로로 시작되는 모든 경로에서 사용 가능
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());


        // 4. code 폐기
        String jti = jwtUtils.getJtiFromOAuth2Code(code);
        redisTemplate.opsForValue()
                .set(jti, code);

        // 5. response
        return new ApiResponse(HttpStatus.OK.value(),
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
