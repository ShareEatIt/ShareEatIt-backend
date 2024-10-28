package com.carpBread.shareEatIt.domain.member.dto.keyword;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder @Getter
public class KeywordResponseDto {
    private Long id;
    private String keyword;
    private Boolean avail;
}
