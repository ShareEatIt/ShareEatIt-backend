package com.carpBread.shareEatIt.domain.chat.stompWebSocket;

import com.carpBread.shareEatIt.domain.auth.LoginProvider;
import com.carpBread.shareEatIt.domain.auth.dto.AuthenticationPrincipal;
import com.carpBread.shareEatIt.domain.auth.util.JWTUtils;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.repository.MemberRepository;
import com.carpBread.shareEatIt.global.exception.CustomException;
import io.jsonwebtoken.JwtException;
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
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import static com.carpBread.shareEatIt.global.exception.CustomExceptionStatus.UNAUTHORIZED_JWT;

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
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                log.error("토큰이 존재하지 않습니다");
                throw new JwtException("토큰이 존재하지 않습니다.");
            }

            String token = authorization.split(" ")[1];

            // 2. 토큰 기한 만료 여부 확인
            if (jwtUtils.isExpired(token)) {
                log.error("토큰 기한이 만료되었습니다.");
                throw new JwtException("토큰 기한이 만료되었습니다.");

            }

            // 3. 로그아웃된 JWT인지 확인 - redis에 포함된 jti인지 확인

            // redis의 key 리스트
            String jti = jwtUtils.getJti(token);
            Set<String> keys = redisTemplate.keys(jti);

            // 해당 jti가 redis에 저장되어 있는 경우 로그아웃된 토큰이라고 파악
            if (!keys.isEmpty()){
                log.error("로그아웃된 토큰입니다. 다시 로그인해주세요.");

                throw new JwtException("로그아웃된 토큰입니다. 다시 로그인해주세요.");
            }


            // 4. 토큰에서 member 객체 추출
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
            // 해당 username 혹은 email에 매칭되는 회원이 존재하지 않는 경우
            if (member==null){
                throw new JwtException("회원가입되어있지 않습니다.");
            }



            // 5. 인증된 사용자 principal security context에 포함
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    new AuthenticationPrincipal(member),
                    sub,
                    Collections.singleton(new SimpleGrantedAuthority("ROLE_MEMBER")));

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        } catch (JwtException e) {
            throw new JwtException("JwtException - jwt 인증 오류");  // preSend에서 catch문에 걸리기 위함
        } catch (Exception e) {
//            throw new CustomException(UNAUTHORIZED_JWT, "Exception - jwt 인증 오류", "/ws" );  // preSend에서 catch문에 걸리기 위함

        }

    }

}