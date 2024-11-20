package com.carpBread.shareEatIt.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @NotBlank
@NoArgsConstructor
public class MemberAvailRequestDto {

    private Boolean isKeywordAvail;
    private Boolean isNoticeAvail;
}
