package com.carpBread.shareEatIt.domain.participation.dto.responseDto;

import com.carpBread.shareEatIt.domain.participation.entity.Participation;
import com.carpBread.shareEatIt.domain.participation.entity.ParticipationStatus;
import com.carpBread.shareEatIt.domain.sharingPost.entity.PostStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class ParticipationUpdateStatusResponseDto {
    private final Long sharingPostId;
    private final PostStatus sharingPostStatus;
    private final Long participationId;
    private final ParticipationStatus participationStatus;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    @Builder
    public ParticipationUpdateStatusResponseDto(Long sharingPostId, PostStatus sharingPostStatus, Long participationId, com.carpBread.shareEatIt.domain.participation.entity.ParticipationStatus participationStatus, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.sharingPostId = sharingPostId;
        this.sharingPostStatus = sharingPostStatus;
        this.participationId = participationId;
        this.participationStatus = participationStatus;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static ParticipationUpdateStatusResponseDto from(Participation participation){
        return ParticipationUpdateStatusResponseDto.builder()
                .sharingPostId(participation.getPost().getId())
                .sharingPostStatus(participation.getPost().getStatus())
                .participationId(participation.getId())
                .participationStatus(participation.getStatus())
                .createdAt(participation.getCreatedAt())
                .modifiedAt(participation.getModifiedAt())
                .build();
    }
}
