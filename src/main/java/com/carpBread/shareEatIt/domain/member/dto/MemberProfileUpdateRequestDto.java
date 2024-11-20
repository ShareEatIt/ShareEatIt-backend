package com.carpBread.shareEatIt.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
@NotBlank
public class MemberProfileUpdateRequestDto {
    private String profileImg;
    private String nickname;
    private String provider;

    private Double latitude;
    private Double longitude;

    private String addressSt;
    private String addressDetail;


}
