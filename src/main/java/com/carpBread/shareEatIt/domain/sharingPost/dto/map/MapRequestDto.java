package com.carpBread.shareEatIt.domain.sharingPost.dto.map;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MapRequestDto {

    @NotNull
    private Double longitude;

    @NotNull
    private Double latitude;
}
