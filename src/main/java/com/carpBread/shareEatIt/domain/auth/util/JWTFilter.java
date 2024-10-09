package com.carpBread.shareEatIt.domain.auth.util;

import com.carpBread.shareEatIt.domain.auth.OAuth2Principal;
import com.carpBread.shareEatIt.domain.auth.OAuth2UserService;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.entity.Provider;
import com.carpBread.shareEatIt.domain.member.repository.MemberRepository;
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
import java.util.Collections;

@AllArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private JWTUtils jwtUtils;
    private MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");

        try {
            // 1. 토큰 유무 확인
            if (authorization==null || !authorization.startsWith("Bearer ")){
                throw new JwtException("토큰이 존재하지 않습니다.");
            }

            String token = authorization.split(" ")[1];

            // 2. 토큰 기한 만료 여부 확인
            if (jwtUtils.isExpired(token)){
                throw new JwtException("토큰 기한이 만료되었습니다.");
            }

            // 3. context authentication에 저장하기
            String email = jwtUtils.getEmail(token);
            Member member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("토큰을 찾을 수 없습니다"));

            String user_email= "yujinalice00@gmail.com";
            if (!email.equals(member.getEmail())){
                throw new JwtException("회원가입되어있지 않습니다.");
            }

            OAuth2Principal principal = new OAuth2Principal(member);

            SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_MEMBER");

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(principal,"kakao", Collections.singleton(grantedAuthority));

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            System.out.println((OAuth2Principal)SecurityContextHolder.getContext().getAuthentication().getPrincipal());


        }catch (JwtException e){
            System.out.println(e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
