package com.carpBread.shareEatIt.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter @Builder
@AllArgsConstructor
public class LocationResponseDtoComponent {
    private String addressSt;
    private String addressDetail;
    private Double latitude;
    private Double longitude;
}
