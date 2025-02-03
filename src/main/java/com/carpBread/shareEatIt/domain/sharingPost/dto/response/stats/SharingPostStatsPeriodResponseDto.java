package com.carpBread.shareEatIt.domain.sharingPost.dto.response.stats;
import lombok.Getter;

import java.util.List;

/* 기간별 개인 나눔 통계 response dto */
@Getter
public class SharingPostStatsPeriodResponseDto {

    // 0. 조회 기간
    private String period;

    // 1. 음식 카테고리별 나눔글 작성 수
    private List<SharingPostPeriodByCategoryResponseComponent> categoryCountList;

    // 2. 가장 많이 나눔한 음식 카테고리
    private String popularCategory;

    // 3. 기간 내 총 나눔 횟수
    private int totalSharedCount;

    // 4. 나눔 성사 비율
    private float participationRate;

    // 5. 10km 이내 거주자 중 나눔 순위
    private int userRankIn10km;

    public SharingPostStatsPeriodResponseDto(String period,
                                             List<SharingPostPeriodByCategoryResponseComponent> categoryCountList,
                                             String popularCategory, int totalSharedCount,
                                             float participationRate, int userRankIn10km) {
        this.period = period;
        this.categoryCountList = categoryCountList;
        this.popularCategory = popularCategory;
        this.totalSharedCount = totalSharedCount;
        this.participationRate = participationRate;
        this.userRankIn10km = userRankIn10km;
    }
}
