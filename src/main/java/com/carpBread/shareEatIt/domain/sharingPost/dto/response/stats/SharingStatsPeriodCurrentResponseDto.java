package com.carpBread.shareEatIt.domain.sharingPost.dto.response.stats;

import lombok.Getter;

/* 사용자의 현재 연도 월별/ 현재 달의 주간별 나눔 수 리스트 */
@Getter
public class SharingStatsPeriodCurrentResponseDto {

    private StatsCurrentMonthResponseComponent monthStats;
    private StatsCurrentYearResponseComponent yearStats;

    public SharingStatsPeriodCurrentResponseDto(StatsCurrentMonthResponseComponent monthStats, StatsCurrentYearResponseComponent yearStats) {
        this.monthStats = monthStats;
        this.yearStats = yearStats;
    }
}
