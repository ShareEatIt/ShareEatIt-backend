package com.carpBread.shareEatIt.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class LogoutResponseDto {
    private String refreshToken;

    public LogoutResponseDto(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
