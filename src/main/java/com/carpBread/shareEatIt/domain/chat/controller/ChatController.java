package com.carpBread.shareEatIt.domain.chat.controller;

import com.carpBread.shareEatIt.domain.auth.annotation.AuthUser;
import com.carpBread.shareEatIt.domain.chat.dto.ChatListResponseDto;
import com.carpBread.shareEatIt.domain.chat.dto.ChatMessageRequestDto;
import com.carpBread.shareEatIt.domain.chat.dto.ChatMessageResponseDto;
import com.carpBread.shareEatIt.domain.chat.service.ChatMessageService;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
public class ChatController {

    private final ChatMessageService chatMessageService;
    private final SimpMessageSendingOperations messagingTemplate;

    /* 채팅 - 메시지 전달*/
    @MessageMapping("/chat/message/{chatRoomId}")  // app/chat/message/{chatRoomId} 로 메세지 발송
    @SendTo("/topic/chatRoom/{chatRoomId}") // 동적으로 chatRoomId에 맞는 경로로 메시지 발송하도록 명확히 지정 (아니면 순환 참조 문제 발생 가능)
    public ChatMessageResponseDto sendMessage(@Payload ChatMessageRequestDto requestDto) {
        ChatMessageResponseDto responseDto = chatMessageService.saveMessage(requestDto);
        messagingTemplate.convertAndSend("/topic/chatRoom/"+ responseDto.getChatRoomId(), responseDto);
        return responseDto;
    }


    /* 해당 채팅방의 채팅 내역 조회 */
    @GetMapping("/chat/message/{chatRoomId}")
    public ApiResponse<ChatListResponseDto> getAllMessageByChatRoomId(@AuthUser Member member,@PathVariable("chatRoomId") Long roomId){
        ChatListResponseDto responseDto = chatMessageService.getMessageByChatRoomId(member, roomId);
        ApiResponse<ChatListResponseDto> response = new ApiResponse<>(
                HttpStatus.OK.value(),  // 상태코드 201
                "채팅 내역 조회",   // 성공 메시지
                responseDto      // 실제 데이터
        );
        return response;
    }

}
