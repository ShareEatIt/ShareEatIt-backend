package com.carpBread.shareEatIt.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class OpponentInfoResponseDto {
    private Long id;
    private String profileImg;
    private String nickname;
}
