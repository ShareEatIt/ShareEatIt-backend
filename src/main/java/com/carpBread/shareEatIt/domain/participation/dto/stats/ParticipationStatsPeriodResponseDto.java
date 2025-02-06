package com.carpBread.shareEatIt.domain.participation.dto.stats;

import lombok.Getter;

import java.util.List;

/* 기간별 개인 참여 통계 response dto */
@Getter
public class ParticipationStatsPeriodResponseDto {

    // 0. 조회 기간
    private String period;

    // 1. 음식 카테고리별 참여 수
    private List<ParticipationPeriodByCategoryResponseComponent> categoryCountList;

    // 2. 가장 많이 참여한 음식 카테고리
    private String popularCategory;

    // 3. 기간 내 총 참여 횟수
    private int totalParticipatedCount;

    // 4. 10km 이내 거주자 중 참여 순위
    private int userRankIn10km;

    public ParticipationStatsPeriodResponseDto(String period,
                                               List<ParticipationPeriodByCategoryResponseComponent> categoryCountList,
                                               String popularCategory,
                                               int totalParticipatedCount,
                                               int userRankIn10km) {
        this.period = period;
        this.categoryCountList = categoryCountList;
        this.popularCategory = popularCategory;
        this.totalParticipatedCount = totalParticipatedCount;
        this.userRankIn10km = userRankIn10km;
    }
}
