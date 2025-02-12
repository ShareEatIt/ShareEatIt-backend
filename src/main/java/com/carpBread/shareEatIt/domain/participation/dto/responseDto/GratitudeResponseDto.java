package com.carpBread.shareEatIt.domain.participation.dto.responseDto;

import com.carpBread.shareEatIt.domain.participation.entity.GratitudeSticker;
import com.carpBread.shareEatIt.domain.participation.entity.GratitudeType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(force = true)
public class GratitudeResponseDto {
    private final Long gratitudeStickersId;
    private final Long sharingPostId;
    private final Long participationId;
    private final Long giverId;
    private final Long reviewerId;
    private final GratitudeType gratitudeType;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    // 빌더 패턴으로 객체 생성
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

    // 정적 팩토리 메서드 방식으로 객체 생성 - *(순서 실수 방지를 위해 메서드 내 빌더 패턴 적용)
    public static GratitudeResponseDto from(GratitudeSticker gratitudeSticker){
        return GratitudeResponseDto.builder()
                .gratitudeStickersId(gratitudeSticker.getId())
                .sharingPostId(gratitudeSticker.getPost().getId())
                .participationId(gratitudeSticker.getParticipation().getId())
                .giverId(gratitudeSticker.getGiver().getId())
                .reviewerId(gratitudeSticker.getReviewer().getId())
                .gratitudeType(gratitudeSticker.getGratitudeType())
                .createdAt(gratitudeSticker.getCreatedAt())
                .modifiedAt(gratitudeSticker.getModifiedAt())
                .build();
    }
}
