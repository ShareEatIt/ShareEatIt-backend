package com.carpBread.shareEatIt.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
@Getter
public class AvailResponseDto {

    private Long id;
    private Boolean isNoticeAvail;
    private Boolean isKeywordAvail;

    public AvailResponseDto(Long id,
                            Boolean isNoticeAvail,
                            Boolean isKeywordAvail) {
        this.id = id;
        this.isNoticeAvail = isNoticeAvail;
        this.isKeywordAvail = isKeywordAvail;
    }
}
