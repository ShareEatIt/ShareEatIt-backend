//package com.carpBread.shareEatIt.domain.chat.stompWebSocket;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.simp.config.ChannelRegistration;
//import org.springframework.messaging.simp.config.MessageBrokerRegistry;
//import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
//import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
//import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
//
//@Configuration
//@EnableWebSocketMessageBroker  // WebSocket 메시지 처리를 가능하게 해주는 메시지 브로커를 활성화
//@RequiredArgsConstructor
//public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
//
//    private final StompHandler stompHandler; // jwt 인증
//
//    @Override
//    public void configureMessageBroker(MessageBrokerRegistry config) {
//        // 메시지 구독 url (topic을 구독)
//        config.enableSimpleBroker("/topic");  // 간단한 메시지 브로커 활성화
//        // 메시지 발행 url
//        config.setApplicationDestinationPrefixes("/app");  // @MessageMapping이 붙은 메서드에 바인딩되는 메시지의 경로를 지정
//    }
//
//    @Override
//    public void registerStompEndpoints(StompEndpointRegistry registry){
//        // 엔드포인트(client가 WebSocket 연결을 요청할 때 사용하는 주소) 주소 설정 , 변경 가능 ex. /chat
//        registry.addEndpoint("/ws")
//                .setAllowedOriginPatterns("*")  // CORS 설정 - 모든 도메인에서 오는 요청 허용
//                .withSockJS();
//        registry.addEndpoint("/ws")
//                .setAllowedOriginPatterns("*"); // 중요...
//    }
//
//    @Override
//    public void confiqureClientInboundChannel(ChannelRegistration registration){
//        registration.interceptors(stompHandler);
//    }
//
//}
