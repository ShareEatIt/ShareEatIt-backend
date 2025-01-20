package com.carpBread.shareEatIt.domain.sharingPost.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
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

    public SharingPostListRequestDto(String postType, Double latitude, Double longitude) {
        this.postType = postType;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
