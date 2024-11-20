package com.carpBread.shareEatIt.global.config;

import com.carpBread.shareEatIt.domain.chat.stompWebSocket.FilterChannelInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker  // WebSocket 메시지 처리를 가능하게 해주는 메시지 브로커를 활성화
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final FilterChannelInterceptor filterChannelInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        // 엔드포인트(client가 WebSocket 연결을 요청할 때 사용하는 주소) 주소 설정
        registry.addEndpoint("/ws")
//                .setAllowedOrigins("https://jiangxy.github.io", "http://localhost:3000", "http://54.180.228.54")
                .setAllowedOriginPatterns("*")  // CORS 설정 - 모든 도메인에서 오는 요청 허용
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 메시지 구독 url (topic을 구독)
        config.enableSimpleBroker("/topic");  // 간단한 메시지 브로커 활성화
        // 메시지 발행 url
        config.setApplicationDestinationPrefixes("/app");  // @MessageMapping이 붙은 메서드에 바인딩되는 메시지의 경로를 지정
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        log.info("소켓 통신 연결 전(CONNECT) 토큰 인증 과정");
        registration.interceptors(filterChannelInterceptor);
    }

}
