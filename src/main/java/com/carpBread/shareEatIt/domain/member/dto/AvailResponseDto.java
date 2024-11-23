package com.carpBread.shareEatIt.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class AvailResponseDto {

    private Long id;
    private Boolean isNoticeAvail;
    private Boolean isKeywordAvail;
}
