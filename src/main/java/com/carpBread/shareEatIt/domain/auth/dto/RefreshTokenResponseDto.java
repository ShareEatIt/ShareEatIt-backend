package com.carpBread.shareEatIt.domain.auth.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenResponseDto {

    private String accessToken;
    private String refreshToken;
}
