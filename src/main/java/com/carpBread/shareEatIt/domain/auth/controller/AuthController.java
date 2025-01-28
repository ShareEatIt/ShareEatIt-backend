package com.carpBread.shareEatIt.domain.auth.controller;

import com.carpBread.shareEatIt.domain.auth.LoginProvider;
import com.carpBread.shareEatIt.domain.auth.dto.request.RefreshRequestDto;
import com.carpBread.shareEatIt.domain.auth.dto.response.RefreshTokenResponseDto;
import com.carpBread.shareEatIt.global.jwt.JWTUtils;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.repository.MemberRepository;
import com.carpBread.shareEatIt.global.exception.CustomException;
import com.carpBread.shareEatIt.global.exception.CustomExceptionStatus;
import com.carpBread.shareEatIt.global.exception.Domain;
import com.carpBread.shareEatIt.global.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

/* 로그아웃, 리프레시 토큰 관련 인증 핸들러 */
// 💡 참고 : 로그인/로그아웃 관련하여 변경사항이 많아 수정된 내용이 많아 코드만 남겨둔 상태. 프런트 연결 후 삭제 예정임.
@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {

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

    /* 리프레시 토큰 발급, 수정 예정!!! */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<RefreshTokenResponseDto>> refreshAccessToken(@RequestBody @Valid RefreshRequestDto dto)throws Exception{

        Member member = memberRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new CustomException(CustomExceptionStatus.NOT_FOUND_MEMBER,
                        "해당 이메일에 맞는 회원 정보를 찾을 수 없습니다",
                        AuthController.class.getSimpleName(),
                        dto.getEmail(),
                        Domain.AUTH)
                );
//        if(jwtUtils.getProvider(dto.getRefreshToken()).equals(LoginProvider.LOCAL.name())){
//            if(! jwtUtils.getSub(dto.getRefreshToken()).equals(member.getUsername())){
//                throw null /* new AppException(ErrorCode.INVALID_REFRESH_TOKEN,"유효하지 않은 리프레시 토큰입니다. 재로그인해주십시오","/auth/refresh")*/;
//            }
//        }
//        else{
//            if (! jwtUtils.getSub(dto.getRefreshToken()).equals(member.getEmail())){
//                throw null /* new AppException(ErrorCode.INVALID_REFRESH_TOKEN,"유효하지 않은 리프레시 토큰입니다. 재로그인해주십시오","/auth/refresh")*/;
//            }
//        }
        if (!member.getRefreshToken().equals(dto.getRefreshToken())){
            throw new CustomException(CustomExceptionStatus.INVALID_REFRESH_TOKEN,
                    "올바르지 않은 리프레시 토큰입니다.",
                    AuthController.class.getSimpleName(),
                    null,
                    Domain.AUTH
                    );
        }

        String newAccessToken = "Bearer "+jwtUtils.createAccessToken(member.getEmail(), LoginProvider.KAKAO);
        String newRefreshToken = jwtUtils.createRefreshToken(member.getEmail(), LoginProvider.KAKAO);
        member.updateRefreshToken(newRefreshToken);
        memberRepository.save(member);

        System.out.println(newAccessToken);
        System.out.println(newRefreshToken);

        RefreshTokenResponseDto refreshTokenResponseDto =  new RefreshTokenResponseDto(newAccessToken, newRefreshToken);

        ApiResponse<RefreshTokenResponseDto> responseDto = new ApiResponse<>(HttpStatus.OK.value(), "리프레시 토큰 발급 성공", refreshTokenResponseDto);

        return ResponseEntity.ok().body(responseDto);

    }
//
//    @GetMapping("/logout")
//    public ResponseEntity<String> logout(HttpServletRequest request){
//        String token = null;
//        String authHeader = request.getHeader("Authorization");
//        if (authHeader != null && authHeader.startsWith("Bearer")) {
//            token = authHeader.split(" ")[1];
//        }else{
//            throw new AppException(ErrorCode.UNAUTHORIZED_JWT,"유효하지 않은 인증 토큰입니다","/logout");
//        }
//
//        String email = jwtUtils.getEmail(token);
//        Member member = memberRepository.findByEmail(email)
//                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_MEMBER, "해당 이메일에 맞는 회원 정보를 찾을 수 없습니다", "/logout"));
//        String accessToken = member.getAccessToken();
//
//        // redis에 jwt 저장 - 블랙리스트
//        String redisKey = "token:"+email+":"+ UUID.randomUUID().toString();
//        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
//        valueOperations.set(redisKey,token);
//
//        // kakao 서버에 '카카오 계정과 함께 로그아웃' 요청 보내기
//
//        if (accessToken != null) {
//            webClient.get()
//                    .uri(kakaoLogoutUrl + "?client_id=" + clientId + "&logout_redirect_uri=" + logoutRedirectUri)
//                    .retrieve()
//                    .bodyToMono(Void.class)
//                    .doOnError(error -> {
//                        // 로그아웃 중 오류가 발생한 경우 로깅
//                        System.err.println("Error during Kakao logout: " + error.getMessage());
//                    })
//                    .subscribe();
//
//            System.out.println("Kakao logout request sent for token: " + token);
//        } else {
//            throw new AppException(ErrorCode.LOGOUT_FAIL, "로그인 토큰이 존재하지 않습니다","/logout");
//        }
//
//
//        return ResponseEntity.ok().body("로그아웃이 성공적으로 완료되었습니다");
//    }
//
}
