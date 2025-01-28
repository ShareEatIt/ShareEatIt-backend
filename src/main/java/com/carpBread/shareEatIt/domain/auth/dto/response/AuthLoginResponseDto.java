package com.carpBread.shareEatIt.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AuthLoginResponseDto {
    private String accessToken;
    private String refreshToken;
    private Boolean isNewMember;

    public AuthLoginResponseDto(String accessToken, String refreshToken, Boolean isNewMember) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.isNewMember = isNewMember;
    }
}
