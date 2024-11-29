package com.carpBread.shareEatIt.domain.chat.dto;

import com.carpBread.shareEatIt.domain.chat.entity.ChatRoom;
import com.carpBread.shareEatIt.domain.chat.entity.ChatRoomStatus;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ChatRoomListDetailResponseDto {
    private Long ChatRoomId;
    private Long participationId;
    private ChatRoomStatus status;
    private String opponent;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    @Builder
    public ChatRoomListDetailResponseDto(Long chatRoomId, Long participationId, ChatRoomStatus status, String opponent, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        ChatRoomId = chatRoomId;
        this.participationId = participationId;
        this.status = status;
        this.opponent = opponent;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static ChatRoomListDetailResponseDto from(ChatRoom chatRoom, Member opponent) {
        return new ChatRoomListDetailResponseDto(
                chatRoom.getId(),
                chatRoom.getParticipation().getId(),
                chatRoom.getStatus(),
                opponent.getNickname(),
                chatRoom.getCreatedAt(),
                chatRoom.getModifiedAt()
        );
    }
}
