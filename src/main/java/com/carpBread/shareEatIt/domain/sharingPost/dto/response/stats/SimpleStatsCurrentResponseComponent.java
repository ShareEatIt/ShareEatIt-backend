package com.carpBread.shareEatIt.domain.sharingPost.dto.response.stats;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;


/* 월별 / 주별 통계 시 해당 월 또는 주차, 횟수를 포함하는 response dto component */
@Getter
public class SimpleStatsCurrentResponseComponent {
    private int unit;
    private Long count;

    @Builder
    public SimpleStatsCurrentResponseComponent(int unit, Long count) {
        this.unit = unit;
        this.count = count;
    }
}
