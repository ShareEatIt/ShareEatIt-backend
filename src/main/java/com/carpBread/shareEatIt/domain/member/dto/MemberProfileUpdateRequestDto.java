package com.carpBread.shareEatIt.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberProfileUpdateRequestDto {
    private String profileImg;
    private String nickname;
    private String addressSt;
    private String addressDetail;
}
