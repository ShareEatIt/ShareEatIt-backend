package com.carpBread.shareEatIt.domain.member.dto.keyword;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Builder @Getter
public class KeywordAvailableListResponseDto {
    private List<KeywordResponseDto> keywordList;

}
