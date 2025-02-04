package com.carpBread.shareEatIt.domain.participation.dto.stats;

import lombok.Getter;

import java.util.List;

/* ParticipationStatsPeriodResponseDto 내 음식 카테고리별 참여 개수 response component */
@Getter
public class ParticipationPeriodByCategoryResponseComponent {

    private String category;
    private int count;

    private List<StatsCategoryParticipationListSimpleResponseComponent> participationList;

    public ParticipationPeriodByCategoryResponseComponent(String category,
                                                          int count,
                                                          List<StatsCategoryParticipationListSimpleResponseComponent> participationList) {
        this.category = category;
        this.count = count;
        this.participationList = participationList;
    }
}
