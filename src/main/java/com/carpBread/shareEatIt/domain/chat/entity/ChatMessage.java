package com.carpBread.shareEatIt.domain.chat.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@SuperBuilder
@Document(collection = "chat")
public class ChatMessage {

    @Id
    private String id;

    private ChatMessageType type; // 메시지 타입

    private Long chatRoomId; // 방 번호

    private Long senderId; // 채팅을 보낸 사람

    private String content; // 메시지

    private LocalDateTime createdAt; // 채팅 발송 시간

    @Builder
    public ChatMessage(ChatMessageType type, Long chatRoomId, Long senderId, String content, LocalDateTime createdAt) {
        this.type = type;
        this.chatRoomId = chatRoomId;
        this.senderId = senderId;
        this.content = content;
        this.createdAt = createdAt;
    }

}
