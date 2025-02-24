package com.carpBread.shareEatIt.domain.chat.dto.responseDto;

import com.carpBread.shareEatIt.domain.chat.entity.ChatMessage;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(force = true)
public class ChatMessageResponseDto {

    private final String ChatMessageId;
    private final Long chatRoomId;
    private final Long senderId; // 채팅을 보낸 사람
    private final String content;
    private final LocalDateTime createdAt;

    @Builder
    public ChatMessageResponseDto(String chatMessageId, Long chatRoomId, Long senderId, String content, LocalDateTime createdAt) {
        ChatMessageId = chatMessageId;
        this.chatRoomId = chatRoomId;
        this.senderId = senderId;
        this.content = content;
        this.createdAt = createdAt;
    }

    public static ChatMessageResponseDto from(ChatMessage message){
        return ChatMessageResponseDto.builder()
                .chatMessageId(message.getId())
                .chatRoomId(message.getChatRoomId())
                .senderId(message.getSenderId())
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
