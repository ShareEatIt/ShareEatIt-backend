package com.carpBread.shareEatIt.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter @Builder
@AllArgsConstructor
public class AuthenticationResponseDto {
    private String accessToken;
    private String refreshToken;
}
