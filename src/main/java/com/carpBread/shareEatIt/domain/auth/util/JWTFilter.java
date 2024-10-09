package com.carpBread.shareEatIt.domain.auth.util;

import com.carpBread.shareEatIt.domain.auth.OAuth2Principal;
import com.carpBread.shareEatIt.domain.auth.OAuth2UserService;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;

@AllArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private JWTUtils jwtUtils;
    private MemberRepository memberRepository;
    private ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");

        try {
            // 1. 토큰 유무 확인
            if (authorization==null || !authorization.startsWith("Bearer ")){

                errorResponse(request,response,ErrorCode.INVALID_ACCESS_TOKEN,"토큰이 존재하지 않습니다.");

                throw new JwtException("토큰이 존재하지 않습니다.");

            }

            String token = authorization.split(" ")[1];

            // 2. 토큰 기한 만료 여부 확인
            if (jwtUtils.isExpired(token)){
                errorResponse(request,response, ErrorCode.INVALID_ACCESS_TOKEN,"토큰 기한이 만료되었습니다.");

                throw new JwtException("토큰 기한이 만료되었습니다.");
            }

            // 3. context authentication에 저장하기
            String email = jwtUtils.getEmail(token);
            Member member = memberRepository.findByEmail(email)
                    .orElse(null);

            if (member==null ||!email.equals(member.getEmail())){

                errorResponse(request,response,ErrorCode.INVALID_ACCESS_TOKEN, "회원가입되어있지 않습니다.");
                throw new JwtException("회원가입되어있지 않습니다.");
            }

            OAuth2Principal principal = new OAuth2Principal(member);

            SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_MEMBER");

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(principal,"kakao", Collections.singleton(grantedAuthority));

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);


        }catch (JwtException e){
            System.out.println(e.getMessage());
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        filterChain.doFilter(request, response);
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
