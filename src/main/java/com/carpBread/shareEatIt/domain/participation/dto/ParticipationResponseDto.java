package com.carpBread.shareEatIt.domain.participation.dto;

import com.carpBread.shareEatIt.domain.chat.entity.ChatRoom;
import com.carpBread.shareEatIt.domain.participation.entity.ParticipationStatus;
import com.carpBread.shareEatIt.domain.participation.entity.Participation;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ParticipationResponseDto {
    private Long participationId;
    private Long sharingPostId;
    private Long giverId;
    private Long receiverId;
    private Long chatRoomId;
    private ParticipationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

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
        return new ParticipationResponseDto(
                participation.getId(),
                participation.getPost().getId(),
                participation.getGiver().getId(),
                participation.getPost().getWriter().getId(),
                chatRoom.getId(),
                participation.getStatus(),
                participation.getCreatedAt(),
                participation.getModifiedAt()
        );
    }
}
