package com.carpBread.shareEatIt.domain.auth.controller;

import com.carpBread.shareEatIt.domain.auth.AuthLoginResponseDto;
import com.carpBread.shareEatIt.domain.auth.AuthUser;
import com.carpBread.shareEatIt.domain.auth.dto.RefreshRequestDto;
import com.carpBread.shareEatIt.domain.auth.util.JWTUtils;
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
    public void refreshAccessToken(@AuthUser Member member,
                                                     @RequestBody @Valid RefreshRequestDto dto,
                                                     HttpServletResponse response)throws Exception{

        if (!member.getRefreshToken().equals(dto.getRefreshToken())){
            throw new AppException(ErrorCode.UNAUTHORIZED_USER,"refreshToken에 대한 사용 권한이 없습니다","/auth/refresh");
        }
        String token = jwtUtils.createToken(member.getEmail(), member.getNickname());
        String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8.toString());


        String newRefreshToken = jwtUtils.createToken(member.getEmail(), member.getNickname());
        member.updateRefreshToken(newRefreshToken);
        memberRepository.save(member);


        Cookie cookie = new Cookie("accessToken",encodedToken);
        cookie.setPath("/");
        cookie.setMaxAge(60*60*24);

        response.addCookie(cookie);
        response.setStatus(HttpServletResponse.SC_OK);
        ApiResponse responseDto = new ApiResponse<AuthLoginResponseDto>(HttpStatus.CREATED.value(), "리프레시 토큰 재발급 성공", new AuthLoginResponseDto(newRefreshToken));
        String jsonResponse = objectMapper.writeValueAsString(responseDto);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
        response.getWriter().close();

    }

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
