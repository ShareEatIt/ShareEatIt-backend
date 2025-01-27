package com.carpBread.shareEatIt.domain.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberAvailRequestDto {

    @NotNull
    private Boolean isKeywordAvail;

    @NotNull
    private Boolean isNoticeAvail;
}
