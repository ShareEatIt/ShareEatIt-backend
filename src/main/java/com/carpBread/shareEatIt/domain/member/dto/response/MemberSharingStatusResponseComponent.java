package com.carpBread.shareEatIt.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberSharingStatusResponseComponent {
    private Long BAKERY;
    private Long BEVERAGE;
    private Long CONVENIENCEFOOD;
    private Long KOREAN;
    private Long JAPANESE;
    private Long CHINESE;
    private Long WESTERN;
    private Long SNACK;
    private Long GROCERIES;
    private Long ETC;

    public MemberSharingStatusResponseComponent(Long BAKERY, Long BEVERAGE,
                                                Long CONVENIENCEFOOD, Long KOREAN,
                                                Long JAPANESE, Long CHINESE,
                                                Long WESTERN, Long SNACK,
                                                Long GROCERIES, Long ETC) {
        this.BAKERY = BAKERY;
        this.BEVERAGE = BEVERAGE;
        this.CONVENIENCEFOOD = CONVENIENCEFOOD;
        this.KOREAN = KOREAN;
        this.JAPANESE = JAPANESE;
        this.CHINESE = CHINESE;
        this.WESTERN = WESTERN;
        this.SNACK = SNACK;
        this.GROCERIES = GROCERIES;
        this.ETC = ETC;
    }
}
