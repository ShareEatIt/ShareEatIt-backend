package com.carpBread.shareEatIt.domain.participation.dto;

import com.carpBread.shareEatIt.domain.participation.entity.Participation;
import com.carpBread.shareEatIt.domain.participation.entity.ParticipationStatus;
import com.carpBread.shareEatIt.domain.sharingPost.entity.PostStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ParticipationUpdateStatusResponseDto {
    private Long sharingPostId;
    private PostStatus SharingPostStatus;
    private Long participationId;
    private ParticipationStatus ParticipationStatus;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    @Builder
    public ParticipationUpdateStatusResponseDto(Long sharingPostId, PostStatus sharingPostStatus, Long participationId, com.carpBread.shareEatIt.domain.participation.entity.ParticipationStatus participationStatus, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.sharingPostId = sharingPostId;
        SharingPostStatus = sharingPostStatus;
        this.participationId = participationId;
        ParticipationStatus = participationStatus;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static ParticipationUpdateStatusResponseDto from(Participation participation){
        return new ParticipationUpdateStatusResponseDto(
                participation.getPost().getId(),
                participation.getPost().getStatus(),
                participation.getId(),
                participation.getStatus(),
                participation.getCreatedAt(),
                participation.getModifiedAt()
        );
    }
}
