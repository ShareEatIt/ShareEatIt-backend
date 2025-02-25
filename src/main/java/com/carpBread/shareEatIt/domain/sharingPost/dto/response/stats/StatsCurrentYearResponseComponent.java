package com.carpBread.shareEatIt.domain.sharingPost.dto.response.stats;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/* 사용자의 현재 연도의 월별 나눔 수 리스트 - SharingStatsPeriodCurrentResponseDto 에 포함되는 요소 */
@Getter
public class StatsCurrentYearResponseComponent {
    // 현재 연도
    private int currentYear;

    List<SimpleStatsCurrentResponseComponent> currentYearStatsList;

    @Builder

    public StatsCurrentYearResponseComponent(int currentYear, List<SimpleStatsCurrentResponseComponent> currentYearStatsList) {
        this.currentYear = currentYear;
        this.currentYearStatsList = currentYearStatsList;
    }
}
