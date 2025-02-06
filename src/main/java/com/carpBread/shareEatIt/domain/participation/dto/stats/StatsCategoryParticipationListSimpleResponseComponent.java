package com.carpBread.shareEatIt.domain.participation.dto.stats;

import lombok.Getter;

import java.time.LocalDateTime;

/* ParticipationPeriodByCategoryResponseComponent 내 참여 정보 simple response component */
@Getter
public class StatsCategoryParticipationListSimpleResponseComponent {
    private Long id;
    private String status;
    private LocalDateTime createdAt;

    public StatsCategoryParticipationListSimpleResponseComponent(Long id,
                                                                 String status,
                                                                 LocalDateTime createdAt) {
        this.id = id;
        this.status = status;
        this.createdAt = createdAt;
    }
}
