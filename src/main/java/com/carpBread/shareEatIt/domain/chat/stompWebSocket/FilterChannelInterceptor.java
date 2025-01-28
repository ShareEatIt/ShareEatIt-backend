package com.carpBread.shareEatIt.domain.chat.stompWebSocket;

import com.carpBread.shareEatIt.domain.auth.LoginProvider;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.repository.MemberRepository;
import com.carpBread.shareEatIt.global.exception.CustomException;
import com.carpBread.shareEatIt.global.exception.CustomExceptionStatus;
import com.carpBread.shareEatIt.global.exception.Domain;
import com.carpBread.shareEatIt.domain.auth.annotation.AuthenticationPrincipal;
import com.carpBread.shareEatIt.global.jwt.JWTFilter;
import com.carpBread.shareEatIt.global.jwt.JWTUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)  // 스프링 시큐리티보다 먼저 처리되도록 우선순위 가장 높게 설정
public class FilterChannelInterceptor implements ChannelInterceptor {

    private final JWTUtils jwtUtils;
    private final MemberRepository memberRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        // STOMP 헤더에 직접 접근
        StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        log.info("로그 >>>>>> headerAccessor : {}", headerAccessor);  // header 내용 확인

        // 검증 - 헤더가 없는 경우
        assert headerAccessor != null; // 헤더가 없는 경우
        log.info("로그 >>>>>> STOMP command : {}", headerAccessor.getCommand());  // CONNECT, SEND, SUBSCRIBE, DISCONNECT 인지 확인

        // 연결(CONNECT)시점에 토큰 인증 과정
        StompCommand command = headerAccessor.getCommand();
        if (command == StompCommand.CONNECT) {
            String authorization = removeBrackets(String.valueOf(headerAccessor.getNativeHeader("Authorization")));
            log.info("로그 >>>>>> authorization(AccessToken): {}", authorization);

            // 토큰 검증
            try {
                checkToken(authorization);
            } catch (Exception e) {
                log.warn("토큰 인증 실패 (Authentication failed): {}", e.getMessage());
                // 연결을 차단하려면 `null`을 반환
                // WebSocket에서는 preSend 메소드에서 null을 반환하면 연결을 차단하는 효과
                return null;
            }
        }
        return message;
    }


    // 헤더에서 토큰 추출
    private String removeBrackets(String token) {
        if (token.startsWith("[") && token.endsWith("]")) {
            return token.substring(1, token.length() - 1);
        }
        return token;
    }


    /* 토큰 인증 - 일반적인 HTTP 메소드가 아니므로 JWTFilter의 인증 과정 사용 불가하여 따로 작성한 것 */
    private void checkToken(String authorization) {

        // 1. 토큰 유무 확인
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new CustomException(CustomExceptionStatus.INVALID_JWT,
                    "HTTP header의 Authorization 필드에 토큰이 존재하지 않습니다.",
                    JWTFilter.class.getName(),
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
                    JWTFilter.class.getName(),
                    null,
                    Domain.AUTH);
        }

        // 3. 로그아웃된 JWT인지 확인 - redis에 포함된 jti인지 확인
        if (isLogout(token)){
            throw new CustomException(CustomExceptionStatus.UNAUTHORIZED_JWT,
                    "로그아웃된 Access Token입니다. 다시 로그인해주십시오.",
                    JWTFilter.class.getName(),
                    null,
                    Domain.AUTH);

        }

        // 4. 토큰에서 member 객체 추출
        Member member = getMemberFromToken(token);

        // token의 sub에 매칭되는 회원이 존재하지 않는 경우
        if (member==null){
            throw new CustomException(CustomExceptionStatus.UNAUTHORIZED_JWT,
                    "Access Token의 sub에 매칭되는 회원 정보가 존재하지 않습니다. 다시 로그인해주십시오.",
                    JWTFilter.class.getName(),
                    null,
                    Domain.AUTH);

        }

        // 5. 인증된 사용자 principal security context에 포함
        includeSecurityContext(member,jwtUtils.getSub(token));
    }


    // 토큰 추출
    private String getToken(String authorization){
        return authorization.split(" ")[1];
    }

    /* Redis에서 로그아웃된 토큰인지 파악 */
    private boolean isLogout(String token){
        // redis의 key 리스트
        String jti = jwtUtils.getJti(token);
        Set<String> keys = redisTemplate.keys(jti);

        // 해당 jti가 redis에 저장되어 있는 경우 로그아웃된 토큰이라고 파악
        if (!keys.isEmpty()){
            log.debug(token+"은 로그아웃된 토큰입니다.");
            return true;
        }
        return false;

    }

    // token에서 Member 객체 추출
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

    // 인증된 사용자 security context에 포함
    private void includeSecurityContext(Member member,String sub){
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                new AuthenticationPrincipal(member),
                sub,
                Collections.singleton(new SimpleGrantedAuthority("ROLE_MEMBER")));

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

    }

}