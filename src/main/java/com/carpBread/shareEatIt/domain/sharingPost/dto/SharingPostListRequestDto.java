package com.carpBread.shareEatIt.domain.sharingPost.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class SharingPostListRequestDto {
    @NotNull
    private String postType;
    @NotNull
    private Double latitude;
    @NotNull
    private Double longitude;
}
