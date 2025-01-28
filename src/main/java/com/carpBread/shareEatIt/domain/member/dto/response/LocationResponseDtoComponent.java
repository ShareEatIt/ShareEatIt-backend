package com.carpBread.shareEatIt.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LocationResponseDtoComponent {
    private String addressSt;
    private String addressDetail;
    private Double latitude;
    private Double longitude;

    public LocationResponseDtoComponent(String addressSt, String addressDetail, Double latitude, Double longitude) {
        this.addressSt = addressSt;
        this.addressDetail = addressDetail;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
