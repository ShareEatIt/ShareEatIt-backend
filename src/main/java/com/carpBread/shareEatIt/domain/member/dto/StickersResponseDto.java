package com.carpBread.shareEatIt.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter @Builder
@AllArgsConstructor
public class StickersResponseDto {
    private Long smile1;
    private Long smile2;
    private Long smile3;
    private Long smile4;
    private Long smile5;

}
