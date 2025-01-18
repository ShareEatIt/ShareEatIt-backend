package com.carpBread.shareEatIt.domain.auth.dto;

import com.carpBread.shareEatIt.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthenticationPrincipal {
    private Member member;
}
