package com.carpBread.shareEatIt.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberAvailRequestDto {

    private Boolean isKeywordAvail;
    private Boolean isNoticeAvail;
}
