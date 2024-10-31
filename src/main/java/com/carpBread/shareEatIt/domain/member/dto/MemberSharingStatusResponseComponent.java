package com.carpBread.shareEatIt.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder @Getter
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


}
