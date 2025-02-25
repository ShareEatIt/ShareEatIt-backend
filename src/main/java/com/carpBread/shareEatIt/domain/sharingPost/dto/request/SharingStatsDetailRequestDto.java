package com.carpBread.shareEatIt.domain.sharingPost.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
public class SharingStatsDetailRequestDto {

    @NotBlank
    private LocalDate startDate;

    @NotBlank
    private LocalDate endDate;
}
