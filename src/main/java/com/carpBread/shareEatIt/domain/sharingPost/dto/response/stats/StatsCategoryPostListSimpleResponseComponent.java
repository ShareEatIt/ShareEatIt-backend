package com.carpBread.shareEatIt.domain.sharingPost.dto.response.stats;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
/* SharingPostPeriodByCategoryResponseComponent 내 post 정보 simple response component */
public class StatsCategoryPostListSimpleResponseComponent {
    private Long id;
    private String status;
    private LocalDateTime createdAt;

    public StatsCategoryPostListSimpleResponseComponent(Long id,
                                                        String status,
                                                        LocalDateTime createdAt) {
        this.id = id;
        this.status = status;
        this.createdAt=createdAt;
    }
}
