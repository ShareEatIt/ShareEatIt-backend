package com.carpBread.shareEatIt.domain.member.dto.keyword;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@AllArgsConstructor
@Builder
public class KeywordAvailableListResponseDto {
    private List<KeywordResponseDto> keywordList;

}
