package com.carpBread.shareEatIt.domain.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberProfileUpdateRequestDto {

    private String profileImg;

    @NotBlank
    private String nickname;

    @NotBlank
    private String provider;

    @NotNull
    private Double latitude;
    @NotNull
    private Double longitude;

    @NotBlank
    private String addressSt;
    @NotBlank
    private String addressDetail;

    public MemberProfileUpdateRequestDto(String profileImg, String nickname,
                                         String provider, Double latitude, Double longitude,
                                         String addressSt, String addressDetail) {
        this.profileImg = profileImg;
        this.nickname = nickname;
        this.provider = provider;
        this.latitude = latitude;
        this.longitude = longitude;
        this.addressSt = addressSt;
        this.addressDetail = addressDetail;
    }
}
