package com.carpBread.shareEatIt.domain.sharingPost.dto.map;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Builder
public class MapRequestDto {

    @NotNull
    private Double longitude;

    @NotNull
    private Double latitude;
}
