package com.carpBread.shareEatIt.domain.participation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ParticipationRequestDto {

    @NotNull(message = "나눔글Id가 필요합니다.")
    private Long sharingPostId;

    public ParticipationRequestDto(Long sharingPostId) {
        this.sharingPostId = sharingPostId;
    }
}
