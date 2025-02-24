package com.carpBread.shareEatIt.domain.chat.dto.responseDto;

import com.carpBread.shareEatIt.domain.chat.entity.ChatRoom;
import com.carpBread.shareEatIt.domain.chat.entity.ChatRoomStatus;
import com.carpBread.shareEatIt.domain.member.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(force = true)
public class ChatRoomListDetailResponseDto {

    private final Long chatRoomId;
    private final Long participationId;
    private final ChatRoomStatus status;
    private final String opponent;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    @Builder
    public ChatRoomListDetailResponseDto(Long chatRoomId, Long participationId, ChatRoomStatus status, String opponent, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.chatRoomId = chatRoomId;
        this.participationId = participationId;
        this.status = status;
        this.opponent = opponent;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static ChatRoomListDetailResponseDto from(ChatRoom chatRoom, Member opponent) {
        return ChatRoomListDetailResponseDto.builder()
                .chatRoomId(chatRoom.getId())
                .participationId(chatRoom.getParticipation().getId())
                .status(chatRoom.getStatus())
                .opponent(opponent.getNickname())
                .createdAt(chatRoom.getCreatedAt())
                .modifiedAt(chatRoom.getModifiedAt())
                .build();
    }
}
