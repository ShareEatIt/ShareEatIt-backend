package com.carpBread.shareEatIt.domain.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AuthLoginResponseDto {
    private String token;
    private String accessToken;
}
