package com.carpBread.shareEatIt.domain.chat.dto;

import com.carpBread.shareEatIt.domain.chat.entity.ChatRoom;
import com.carpBread.shareEatIt.domain.chat.entity.ChatRoomStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ChatRoomResponseDto {
    private Long ChatRoomId;
    private Long participationId;
    private ChatRoomStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    @Builder
    public ChatRoomResponseDto(Long chatRoomId, Long participationId, ChatRoomStatus status, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        ChatRoomId = chatRoomId;
        this.participationId = participationId;
        this.status = status;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static ChatRoomResponseDto from(ChatRoom chatRoom) {
        return new ChatRoomResponseDto(
                chatRoom.getId(),
                chatRoom.getParticipation().getId(),
                chatRoom.getStatus(),
                chatRoom.getCreatedAt(),
                chatRoom.getModifiedAt()
        );
    }
}
