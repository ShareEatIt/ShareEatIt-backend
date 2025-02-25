package com.carpBread.shareEatIt.global.jwt;

import com.carpBread.shareEatIt.domain.auth.LoginProvider;
import com.carpBread.shareEatIt.domain.auth.annotation.AuthenticationPrincipal;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.repository.MemberRepository;
import com.carpBread.shareEatIt.global.exception.CustomException;
import com.carpBread.shareEatIt.global.exception.CustomExceptionStatus;
import com.carpBread.shareEatIt.global.exception.Domain;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtils jwtUtils;
    private final MemberRepository memberRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private final String ACCESS_TOKEN_NAME = "AccessToken";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, CustomException {

        // 클라이언트 IP 주소 추출
        String clientIp = request.getRemoteAddr();
        // User-Agent 추출
        String userAgent = request.getHeader("User-Agent");
        System.out.println(clientIp);
        System.out.println(userAgent);

        if (isOmissionUrl(request,response,filterChain)){
            filterChain.doFilter(request, response);
            return;
        }


        String authorization = request.getHeader("Authorization");


        System.out.println("Authorization : "+authorization);

        // 1. 토큰 유무 확인
        if (authorization==null || !authorization.startsWith("Bearer ")){

            throw new CustomException(CustomExceptionStatus.INVALID_JWT,
                    "HTTP header의 Authorization 필드에 토큰이 존재하지 않습니다.",
                    this.getClass().getSimpleName(),
                    null,
                    Domain.AUTH);
        }

        String token = getToken(authorization);

        // 2. 토큰 기한 만료 여부 확인
        try{
            jwtUtils.isExpired(token);
        }catch (Exception e){
            throw new CustomException(CustomExceptionStatus.UNAUTHORIZED_JWT,
                    "Access Token의 유효 기간이 만료되었습니다. 다시 로그인해주십시오.",
                    this.getClass().getSimpleName(),
                    null,
                    Domain.AUTH);
        }

        // 3. 로그아웃된 JWT인지 확인 - redis에 포함된 jti인지 확인
        if (isLogout(token)){
            throw new CustomException(CustomExceptionStatus.UNAUTHORIZED_JWT,
                    "로그아웃된 Access Token입니다. 다시 로그인해주십시오.",
                    this.getClass().getSimpleName(),
                    null,
                    Domain.AUTH);

        }


        // 4. 토큰에서 member 객체 추출
        Member member = getMemberFromToken(token);

        // token의 sub에 매칭되는 회원이 존재하지 않는 경우
        if (member==null){
            throw new CustomException(CustomExceptionStatus.UNAUTHORIZED_JWT,
                    "Access Token의 sub에 매칭되는 회원 정보가 존재하지 않습니다. 다시 로그인해주십시오.",
                    this.getClass().getSimpleName(),
                    null,
                    Domain.AUTH);

        }

        // 5. 인증된 사용자 principal security context에 포함
        includeSecurityContext(member,jwtUtils.getSub(token));


        filterChain.doFilter(request, response);
    }

    /* 토큰 검증을 생략할 경로인지 판단 */
    private boolean isOmissionUrl(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{
        // 토큰 검증을 생략할 경로
        if (
                request.getRequestURI().startsWith("/login")
                || request.getRequestURI().startsWith("/favicon.ico")
                || request.getRequestURI().startsWith("/oauth2/authorize")
                || request.getRequestURI().equals("/oauth2/access-token")
                || request.getRequestURI().startsWith("/ws")
                || request.getRequestURI().startsWith("/auth/refresh")
                || request.getRequestURI().startsWith("/sentry")
                || request.getRequestURI().startsWith("/actuator/health")
                || request.getRequestURI().equals("/")
                || request.getRequestURI().startsWith("/signup")
                ) {
            return true;
        }
        return false;

    }

    /* 인증된 사용자 security context에 포함 */
    private void includeSecurityContext(Member member,String sub){
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                new AuthenticationPrincipal(member),
                sub,
                Collections.singleton(new SimpleGrantedAuthority("ROLE_MEMBER")));

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

    }

    /* token에서 Member 객체 추출 */
    private Member getMemberFromToken(String token){
        String provider = jwtUtils.getProvider(token);
        String sub = jwtUtils.getSub(token);
        Member member=null;

        // local 어플리케이션 자체 로그인으로 로그인한 경우
        if (provider.equals(LoginProvider.LOCAL.name())) {
            member=memberRepository.findByUsername(sub)
                    .orElse(null);

        }
        // oauth2 social 로그인으로 로그인한 경우
        else{
            member = memberRepository.findByEmail(sub)
                    .orElse(null);

        }

        return member;
    }

    /* Redis에서 로그아웃된 토큰인지 파악 */
    private boolean isLogout(String token){
        // redis의 key 리스트
        String jti = jwtUtils.getJti(token);
        Set<String> keys = redisTemplate.keys(jti);

        // 해당 jti가 redis에 저장되어 있는 경우 로그아웃된 토큰이라고 파악
        if (!keys.isEmpty()){
            log.debug("이미 로그아웃된 토큰입니다.");
            return true;
        }
        return false;

    }

    /* 토큰 추출 */
    private String getToken(String authorization){
        return authorization.split(" ")[1];
    }

}
