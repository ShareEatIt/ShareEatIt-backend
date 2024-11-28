package com.carpBread.shareEatIt.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberProfileUpdateRequestDto {
    private String profileImg;

    @NotBlank
    private String nickname;

    @NotBlank
    private String provider;

    @NotBlank
    private Double latitude;
    @NotBlank
    private Double longitude;

    @NotBlank
    private String addressSt;
    @NotBlank
    private String addressDetail;


}
