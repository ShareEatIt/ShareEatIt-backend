package com.carpBread.shareEatIt.domain.auth.util;

import com.carpBread.shareEatIt.domain.auth.LoginProvider;
import com.carpBread.shareEatIt.domain.auth.dto.AuthenticationPrincipal;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.repository.MemberRepository;
import com.carpBread.shareEatIt.global.exception.CustomException;
import com.carpBread.shareEatIt.global.exception.CustomExceptionStatus;
import com.carpBread.shareEatIt.global.exception.Domain;
import com.carpBread.shareEatIt.global.exception.ExceptionResponseDto;
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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, CustomException {

        log.debug(request.getRequestURI());

        if (isOmissionUrl(request,response,filterChain)){
            filterChain.doFilter(request, response);
            return;
        }

        String authorization = request.getHeader("Authorization");

        try {
            // 1. 토큰 유무 확인
            if (authorization==null || !authorization.startsWith("Bearer ")){

                errorResponse(request,response, CustomExceptionStatus.INVALID_ACCESS_TOKEN,"토큰이 존재하지 않습니다.");
                log.error("토큰이 존재하지 않습니다");

                throw new JwtException("토큰이 존재하지 않습니다.");

            }

            String token = getToken(authorization);
            
            // 2. 토큰 기한 만료 여부 확인
            if (jwtUtils.isExpired(token)){
                errorResponse(request,response, CustomExceptionStatus.INVALID_ACCESS_TOKEN,"토큰 기한이 만료되었습니다.");
                log.error("토큰 기한이 만료되었습니다");

                throw new JwtException("토큰 기한이 만료되었습니다.");
            }

            // 3. 로그아웃된 JWT인지 확인 - redis에 포함된 jti인지 확인
            if (isLogout(token)){
                throw new JwtException("로그아웃된 토큰입니다. 다시 로그인해주세요.");
            }


            // 4. 토큰에서 member 객체 추출
            Member member = getMemberFromToken(token);

            // 해당 username 혹은 email에 매칭되는 회원이 존재하지 않는 경우
            if (member==null){

                errorResponse(request,response, CustomExceptionStatus.INVALID_ACCESS_TOKEN, "회원가입되어있지 않습니다.");
                throw new JwtException("회원가입되어있지 않습니다.");
            }

            // 5. 인증된 사용자 principal security context에 포함
            includeSecurityContext(member,jwtUtils.getSub(token));

        }catch (JwtException e){
            throw new CustomException(CustomExceptionStatus.UNAUTHORIZED_JWT,
                    e.getMessage(),
                    JWTFilter.class.getName(),
                    authorization,
                    Domain.AUTH);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /* 토큰 검증을 생략할 경로인지 판단 */
    private boolean isOmissionUrl(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{
        // 토큰 검증을 생략할 경로
        if (request.getRequestURI().startsWith("/login")
                || request.getRequestURI().startsWith("/favicon.ico")
                || request.getRequestURI().startsWith("/oauth2/authorize")
                || request.getRequestURI().startsWith("/ws")
                || request.getRequestURI().startsWith("/auth/refresh")
                || request.getRequestURI().startsWith("/oauth2")
                || request.getRequestURI().startsWith("/sentry")
                || request.getRequestURI().startsWith("/signup")) {
            System.out.println("검증을 생략합니다. \n requesturl : "+request.getRequestURI()+"\n 파일 위치 : JWTFilter.java");

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

    /* 로그아웃된 토큰인지 파악 */
    private boolean isLogout(String token){
        // redis의 key 리스트
        String jti = jwtUtils.getJti(token);
        Set<String> keys = redisTemplate.keys(jti);

        // 해당 jti가 redis에 저장되어 있는 경우 로그아웃된 토큰이라고 파악
        if (!keys.isEmpty()){
            log.error("로그아웃된 토큰입니다. 다시 로그인해주세요.");
            return true;
        }
        return false;

    }

    /* 토큰 추출 */
    private String getToken(String authorization){
        return authorization.split(" ")[1];
    }

    private void errorResponse(HttpServletRequest request, HttpServletResponse response, CustomExceptionStatus customExceptionStatus, String message) throws Exception{


        ExceptionResponseDto responseDto = null;

        String responseJson = objectMapper.writeValueAsString(responseDto);

        response.setStatus(customExceptionStatus.getStatus().value());
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().write(responseJson);
        response.getWriter().flush();
        response.getWriter().close();
    }

}
