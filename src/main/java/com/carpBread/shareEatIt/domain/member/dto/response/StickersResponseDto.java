package com.carpBread.shareEatIt.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
public class StickersResponseDto {
    private Long smile1;
    private Long smile2;
    private Long smile3;
    private Long smile4;
    private Long smile5;

    public StickersResponseDto(Long smile1, Long smile2, Long smile3, Long smile4, Long smile5) {
        this.smile1 = smile1;
        this.smile2 = smile2;
        this.smile3 = smile3;
        this.smile4 = smile4;
        this.smile5 = smile5;
    }
}
