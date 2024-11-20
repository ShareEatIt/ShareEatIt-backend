package com.carpBread.shareEatIt.domain.chat.stompWebSocket;

import com.carpBread.shareEatIt.domain.auth.OAuth2Principal;
import com.carpBread.shareEatIt.domain.auth.util.JWTUtils;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.repository.MemberRepository;
import com.carpBread.shareEatIt.global.exception.AppException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import static com.carpBread.shareEatIt.global.exception.ErrorCode.UNAUTHORIZED_JWT;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class FilterChannelInterceptor implements ChannelInterceptor {

    private final JWTUtils jwtUtils;
    private final MemberRepository memberRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        // STOMP 헤더에 직접 접근
        StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        log.info("로그 >>>>>> headerAccessor : {}", headerAccessor);  // header 내용 확인

        assert headerAccessor != null;
        log.info("로그 >>>>>> headAccessorHeaders : {}", headerAccessor.getCommand());  // CONNECT, SEND 인지 확인

        // StompCommand 를 통해 연결(CONNECT)시점에 토큰을 확인하는 과정
        if (Objects.equals(headerAccessor.getCommand(), StompCommand.CONNECT)) { // 문제 발생 예상 지점
            String authorization = removeBrackets(String.valueOf(headerAccessor.getNativeHeader("Authorization")));
            log.info("로그 >>>>>> authorization(AccessToken): {}", authorization);

            // 토큰 검증
            try {
                checkToken(authorization);

            } catch (Exception e) {
                log.error("토큰 인증 실패 (Authentication failed): {}", e.getMessage());
                // 에러 응답을 보내고 연결을 종료
                headerAccessor.setLeaveMutable(true);  // 헤더를 수정 가능하게 설정
                headerAccessor.setMessage("토큰 인증 실패" + e.getMessage());
                // 예외 발생 시, 연결 종료
                return new GenericMessage<>("토큰 인증 실패" + e.getMessage());
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


    // 토큰 인증 - 일반적인 HTTP 메소드가 아니므로 JWTFilter의 인증 과정 사용 불가하여 따로 작성한 것
    private void checkToken(String authorization) {

        try {
            // 1. 토큰 유무 확인
            if (authorization == null || !authorization.startsWith("Bearer")) {
                log.error("토큰이 존재하지 않습니다");
                throw new JwtException("토큰이 존재하지 않습니다.");
            }

            String token = authorization.split(" ")[1];

            // 2. 토큰 기한 만료 여부 확인
            if (jwtUtils.isExpired(token)) {
                log.error("토큰 기한이 만료되었습니다.");
                throw new JwtException("토큰 기한이 만료되었습니다.");

            }

            // 3. context authentication에 저장하기
            String email = jwtUtils.getEmail(token);
            Member member = memberRepository.findByEmail(email)
                    .orElse(null);

            // 3-1 * : 로그아웃된 JWT인지 확인
            Set<String> keys = redisTemplate.keys("token:" + email + ":*");

            if (keys != null) {
                for (String key : keys) {
                    String logoutToken = (String) redisTemplate.opsForValue().get(key);
                    if (token.equals(logoutToken)) {
                        log.error("로그아웃된 토큰입니다. 다시 로그인해주세요.");
                        throw new JwtException("로그아웃된 토큰입니다. 다시 로그인해주세요.");
                    }
                }
            }

            if (member == null || !email.equals(member.getEmail())) {
                log.error("회원가입되어있지 않습니다.");
                throw new JwtException("회원가입 되어있지 않습니다.");
            }

            OAuth2Principal principal = new OAuth2Principal(member);
            SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_MEMBER");
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(principal, "kakao", Collections.singleton(grantedAuthority));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        } catch (JwtException e) {
            System.out.println("================= jwt 필터에서 오류가 납니다. jwtException 중 하나 " + e.getMessage());
            throw new JwtException("JwtException - jwt 인증 오류");  // preSend에서 catch문에 걸리기 위함
        } catch (Exception e) {
            System.out.println("================= jwt 필터에서 오류가 납니다. 그냥 exception 중 하나 " + e.getMessage());
            throw new AppException(UNAUTHORIZED_JWT, "Exception - jwt 인증 오류", "/ws" );  // preSend에서 catch문에 걸리기 위함

        }

    }

}