package com.carpBread.shareEatIt.domain.auth.annotation;

import com.carpBread.shareEatIt.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class AuthenticationPrincipal {
    private Member member;

    public AuthenticationPrincipal(Member member) {
        this.member = member;
    }
}
