package com.carpBread.shareEatIt.domain.member.dto.request;

import com.carpBread.shareEatIt.domain.member.annotation.Email;
import com.carpBread.shareEatIt.domain.member.annotation.Password;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class SignUpRequestDto {

    @Email
    private String email;

    @NotBlank
    private String username;

    @NotBlank
    @Password
    private String password;

    @NotBlank
    private String nickname;

    @NotNull
    private Boolean isKeywordAvail;

    @NotNull
    private Boolean isNoticeAvail;

    @NotBlank
    private String addressSt;
    private String addressDetail;

    @NotNull
    private Double latitude;
    @NotNull
    private Double longitude;

    @NotNull
    private String provider;

}
