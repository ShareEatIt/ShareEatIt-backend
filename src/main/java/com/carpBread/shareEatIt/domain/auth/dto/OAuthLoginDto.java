package com.carpBread.shareEatIt.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder @Getter
public class OAuthLoginDto {
    private String nickname;
    private String email;
}
