package com.carpBread.shareEatIt.domain.participation.dto.responseDto;

import com.carpBread.shareEatIt.domain.chat.entity.ChatRoom;
import com.carpBread.shareEatIt.domain.participation.entity.ParticipationStatus;
import com.carpBread.shareEatIt.domain.participation.entity.Participation;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
public class ParticipationResponseDto {
    private final Long participationId;
    private final Long sharingPostId;
    private final Long giverId;
    private final Long receiverId;
    private final Long chatRoomId;
    private final ParticipationStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    @Builder
    public ParticipationResponseDto(Long participationId, Long sharingPostId, Long giverId, Long receiverId, Long chatRoomId, ParticipationStatus status, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.participationId = participationId;
        this.sharingPostId = sharingPostId;
        this.giverId = giverId;
        this.receiverId = receiverId;
        this.chatRoomId = chatRoomId;
        this.status = status;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static ParticipationResponseDto from(Participation participation, ChatRoom chatRoom){
        return ParticipationResponseDto.builder()
                .participationId(participation.getId())
                .sharingPostId(participation.getPost().getId())
                .giverId(participation.getGiver().getId())
                .receiverId(participation.getReceiver().getId())
                .chatRoomId(chatRoom.getId())
                .status(participation.getStatus())
                .createdAt(participation.getCreatedAt())
                .modifiedAt(participation.getModifiedAt())
                .build();
    }
}
