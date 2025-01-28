package com.carpBread.shareEatIt.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OpponentInfoResponseDto {
    private Long id;
    private String profileImg;
    private String nickname;

    public OpponentInfoResponseDto(Long id, String profileImg, String nickname) {
        this.id = id;
        this.profileImg = profileImg;
        this.nickname = nickname;
    }
}
