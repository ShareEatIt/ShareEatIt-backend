package com.carpBread.shareEatIt.domain.auth.util;

import com.carpBread.shareEatIt.domain.auth.OAuth2Principal;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.entity.Provider;
import com.carpBread.shareEatIt.domain.member.repository.MemberRepository;
import com.carpBread.shareEatIt.global.exception.AppException;
import com.carpBread.shareEatIt.global.exception.ErrorCode;
import com.carpBread.shareEatIt.global.exception.ErrorResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

@Slf4j
@AllArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private JWTUtils jwtUtils;
    private MemberRepository memberRepository;
    private ObjectMapper objectMapper;
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, AppException {

        if (isOmissionUrl(request,response,filterChain)){
            filterChain.doFilter(request, response);
            return;
        }
        String authorization = request.getHeader("Authorization");

        try {
            // 1. 토큰 유무 확인
            if (authorization==null || !authorization.startsWith("Bearer ")){

                System.out.println("JWTFilter.doFilterInternal");
                System.out.println(authorization);

                errorResponse(request,response,ErrorCode.INVALID_ACCESS_TOKEN,"토큰이 존재하지 않습니다.");
                log.error("토큰이 존재하지 않습니다");

                throw new JwtException("토큰이 존재하지 않습니다.");

            }

            String token = authorization.split(" ")[1];
            
            // 2. 토큰 기한 만료 여부 확인
            if (jwtUtils.isExpired(token)){
                errorResponse(request,response, ErrorCode.INVALID_ACCESS_TOKEN,"토큰 기한이 만료되었습니다.");
                log.error("토큰 기한이 만료되었습니다");

                throw new JwtException("토큰 기한이 만료되었습니다.");
            }

            // 3. context authentication에 저장하기
            String email = jwtUtils.getEmail(token);
            Member member = memberRepository.findByEmail(email)
                    .orElse(null);
            
            // 3-1 * : 로그아웃된 JWT인지 확인
            Set<String> keys = redisTemplate.keys("token:" + email + ":*");

            if (keys!=null){
                for (String key:keys){
                    String logoutToken = (String)redisTemplate.opsForValue().get(key);

                    if (token.equals(logoutToken)){
                        log.error("로그아웃된 토큰입니다. 다시 로그인해주세요.");

                        throw new JwtException("로그아웃된 토큰입니다. 다시 로그인해주세요.");

                    }

                }
            }


            if (member==null ||!email.equals(member.getEmail())){

                errorResponse(request,response,ErrorCode.INVALID_ACCESS_TOKEN, "회원가입되어있지 않습니다.");
                throw new JwtException("회원가입되어있지 않습니다.");
            }

            OAuth2Principal principal = new OAuth2Principal(member);

            SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_MEMBER");

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(principal,"kakao", Collections.singleton(grantedAuthority));

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);


        }catch (JwtException e){
            throw new AppException(ErrorCode.UNAUTHORIZED_JWT,e.getMessage(),request.getRequestURI());
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private boolean isOmissionUrl(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{
        // 토큰 검증을 생략할 경로
        if (request.getRequestURI().startsWith("/login")
                || request.getRequestURI().startsWith("/favicon.ico")
                || request.getRequestURI().startsWith("/oauth2/authorize")
                || request.getRequestURI().startsWith("/ws")) {

            return true;
        }

        return false;

    }

    private void errorResponse(HttpServletRequest request, HttpServletResponse response, ErrorCode errorCode, String message) throws Exception{


        ErrorResponseDto responseDto = ErrorResponseDto.builder()
                .timestamp(LocalDateTime.now())
                .status(errorCode.getStatus().value())
                .message(message)
                .path(request.getRequestURI())
                .build();

        String responseJson = objectMapper.writeValueAsString(responseDto);

        response.setStatus(errorCode.getStatus().value());
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().write(responseJson);
        response.getWriter().flush();
        response.getWriter().close();
    }

}
