package com.carpBread.shareEatIt.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter @Builder
@AllArgsConstructor
public class MemberWithdrawalResponseDto {
    private Long id;
    private String email;
    private String nickname;
}
