package com.carpBread.shareEatIt.domain.chat.dto;

import com.carpBread.shareEatIt.domain.chat.entity.ChatMessage;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ChatMessageResponseDto {
    private String ChatMessageId;
    private Long chatRoomId; // 방 번호
    private Long senderId; // 채팅을 보낸 사람
    private String content; // 메시지
    private LocalDateTime createdAt;

    @Builder
    public ChatMessageResponseDto(String chatMessageId, Long chatRoomId, Long senderId, String content, LocalDateTime createdAt) {
        ChatMessageId = chatMessageId;
        this.chatRoomId = chatRoomId;
        this.senderId = senderId;
        this.content = content;
        this.createdAt = createdAt;
    }

    public static ChatMessageResponseDto from(ChatMessage chatMessage){
        return new ChatMessageResponseDto(
                chatMessage.getId(),
                chatMessage.getChatRoomId(),
                chatMessage.getSenderId(),
                chatMessage.getContent(),
                chatMessage.getCreatedAt()
        );
    }
}
