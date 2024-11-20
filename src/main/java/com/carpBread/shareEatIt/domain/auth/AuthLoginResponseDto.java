package com.carpBread.shareEatIt.domain.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter @Builder
public class AuthLoginResponseDto {
    private String accessToken;
    private String refreshToken;
    private Boolean isNewMember;
}
