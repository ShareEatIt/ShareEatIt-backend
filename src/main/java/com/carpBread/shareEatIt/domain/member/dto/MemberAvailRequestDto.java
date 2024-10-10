package com.carpBread.shareEatIt.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter @Builder
@AllArgsConstructor
public class MemberAvailRequestDto {

    private Boolean isKeywordAvail;
    private Boolean isNoticeAvail;
}
