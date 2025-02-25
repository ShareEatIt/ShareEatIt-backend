package com.carpBread.shareEatIt.domain.sharingPost.dto.response.stats;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/* 사용자의 현재 달의 주간별 나눔 수 리스트 - SharingStatsPeriodCurrentResponseDto 에 포함되는 요소 */
@Getter
public class StatsCurrentMonthResponseComponent {
    // 현재 달
    private int currentMonth;

    List<SimpleStatsCurrentResponseComponent> currentMonthStatsList;

    @Builder
    public StatsCurrentMonthResponseComponent(int currentMonth, List<SimpleStatsCurrentResponseComponent> currentMonthStatsList) {
        this.currentMonth = currentMonth;
        this.currentMonthStatsList = currentMonthStatsList;
    }
}
