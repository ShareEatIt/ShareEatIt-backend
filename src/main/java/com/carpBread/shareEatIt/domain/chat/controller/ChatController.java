package com.carpBread.shareEatIt.domain.chat.controller;

import com.carpBread.shareEatIt.domain.auth.AuthUser;
import com.carpBread.shareEatIt.domain.chat.dto.ChatListResponseDto;
import com.carpBread.shareEatIt.domain.chat.dto.ChatMessageRequestDto;
import com.carpBread.shareEatIt.domain.chat.dto.ChatMessageResponseDto;
import com.carpBread.shareEatIt.domain.chat.service.ChatMessageService;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class ChatController {

    public final ChatMessageService chatMessageService;

    /* 채팅 */
//    @MessageMapping("/chat/message")  // pub/chat/message 로 메세지 발송
//    @SendTo("/topic/chatRoom/{roomId}")
    @PostMapping("/chat/message/{chatRoomId}")
    public ResponseEntity<ApiResponse<ChatMessageResponseDto>> sendMessage(@AuthUser Member member,
                                                                           @PathVariable("chatRoomId") Long roomId,
                                                                           @RequestBody @Valid ChatMessageRequestDto requestDto) {
        ChatMessageResponseDto responseDto = chatMessageService.saveMessage(member, roomId, requestDto);
        ApiResponse<ChatMessageResponseDto> response = new ApiResponse<>(
                HttpStatus.CREATED.value(),  // 상태코드 201
                "채팅 메시지 저장 성공",   // 성공 메시지
                responseDto      // 실제 데이터
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    /* 해당 채팅방의 채팅 내역 조회 */
    @GetMapping("/chat/message/{chatRoomId}")
    public ResponseEntity<ApiResponse<ChatListResponseDto>> getAllMessageByChatRoomId(@AuthUser Member member,@PathVariable("chatRoomId") Long roomId){
        ChatListResponseDto responseDto = chatMessageService.getMessageByChatRoomId(member, roomId);
        ApiResponse<ChatListResponseDto> response = new ApiResponse<>(
                HttpStatus.OK.value(),  // 상태코드 201
                "채팅 내역 조회",   // 성공 메시지
                responseDto      // 실제 데이터
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
