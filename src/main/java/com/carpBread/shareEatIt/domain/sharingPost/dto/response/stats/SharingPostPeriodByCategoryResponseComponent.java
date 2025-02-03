package com.carpBread.shareEatIt.domain.sharingPost.dto.response.stats;

import com.carpBread.shareEatIt.domain.sharingPost.dto.response.stats.StatsCategoryPostListSimpleResponseComponent;
import lombok.Getter;

import java.util.List;

@Getter
/* SharingPostStatsPeriodResponseDto 내 음식 카테고리별 나눔 개수 response component */
public class SharingPostPeriodByCategoryResponseComponent {
    private String category;
    private int count;

    private List<StatsCategoryPostListSimpleResponseComponent> sharingPostList;

    public SharingPostPeriodByCategoryResponseComponent(String category, int count,
                                                        List<StatsCategoryPostListSimpleResponseComponent> sharingPostList) {
        this.category = category;
        this.count = count;
        this.sharingPostList = sharingPostList;
    }
}
