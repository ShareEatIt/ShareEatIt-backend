package com.carpBread.shareEatIt.domain.member.dto.keyword;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class KeywordCreateRequestDto {
    @NotBlank
    private String keyword;
}
