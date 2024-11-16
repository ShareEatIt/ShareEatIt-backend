package com.carpBread.shareEatIt.domain.participation.dto;

import com.carpBread.shareEatIt.domain.participation.entity.GratitudeSticker;
import com.carpBread.shareEatIt.domain.participation.entity.GratitudeType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class GratitudeResponseDto {
    private Long gratitudeStickersId;
    private Long sharingPostId;
    private Long participationId;
    private Long giverId;
    private Long reviewerId;
    private GratitudeType gratitudeType;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    @Builder
    public GratitudeResponseDto(Long gratitudeStickersId, Long sharingPostId, Long participationId, Long giverId, Long reviewerId, GratitudeType gratitudeType, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.gratitudeStickersId = gratitudeStickersId;
        this.sharingPostId = sharingPostId;
        this.participationId = participationId;
        this.giverId = giverId;
        this.reviewerId = reviewerId;
        this.gratitudeType = gratitudeType;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static GratitudeResponseDto from(GratitudeSticker gratitudeSticker){
        return new GratitudeResponseDto(
                gratitudeSticker.getId(),
                gratitudeSticker.getPost().getId(),
                gratitudeSticker.getParticipation().getId(),
                gratitudeSticker.getGiver().getId(),
                gratitudeSticker.getReviewer().getId(),
                gratitudeSticker.getGratitudeType(),
                gratitudeSticker.getCreatedAt(),
                gratitudeSticker.getModifiedAt()
        );
    }
}
