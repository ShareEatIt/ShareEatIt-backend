package com.carpBread.shareEatIt.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder @Getter
public class AvailResponseDto {

    private Long id;
    private Boolean isNoticeAvail;
    private Boolean isKeywordAvail;
}
