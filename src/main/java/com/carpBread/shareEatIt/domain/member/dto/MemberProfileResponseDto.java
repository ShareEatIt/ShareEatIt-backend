package com.carpBread.shareEatIt.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter @Builder
@AllArgsConstructor
public class MemberProfileResponseDto {
    private String profileImg;
    private String nickname;
    private String email;
    private LocationResponseDto location;

}
