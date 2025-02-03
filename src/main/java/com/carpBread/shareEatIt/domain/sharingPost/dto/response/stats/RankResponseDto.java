package com.carpBread.shareEatIt.domain.sharingPost.dto.response.stats;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class RankResponseDto {
    private int rank;

    public RankResponseDto(int rank) {
        this.rank = rank;
    }
}
