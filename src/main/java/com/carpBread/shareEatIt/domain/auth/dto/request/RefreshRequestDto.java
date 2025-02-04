package com.carpBread.shareEatIt.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/* refresh token으로 accessToken을 새로 발급받기 위한 refreshrequest dto */
@Getter
@NoArgsConstructor
public class RefreshRequestDto {

    @NotBlank
    private String email;

    @NotBlank
    private String refreshToken;
}
