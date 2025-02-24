package com.carpBread.shareEatIt.domain.chat.dto.responseDto;

import com.carpBread.shareEatIt.domain.chat.entity.ChatRoom;
import com.carpBread.shareEatIt.domain.chat.entity.ChatRoomStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(force = true)
public class ChatRoomResponseDto {

    private final Long chatRoomId;
    private final Long participationId;
    private final ChatRoomStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    @Builder
    public ChatRoomResponseDto(Long chatRoomId, Long participationId, ChatRoomStatus status, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.chatRoomId = chatRoomId;
        this.participationId = participationId;
        this.status = status;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static ChatRoomResponseDto from(ChatRoom chatRoom) {
        return ChatRoomResponseDto.builder()
                .chatRoomId(chatRoom.getId())
                .participationId(chatRoom.getParticipation().getId())
                .status(chatRoom.getStatus())
                .createdAt(chatRoom.getCreatedAt())
                .modifiedAt(chatRoom.getModifiedAt())
                .build();
    }
}
