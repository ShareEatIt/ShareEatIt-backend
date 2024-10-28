package com.carpBread.shareEatIt.domain.sharingPost.dto.map;

import com.carpBread.shareEatIt.domain.member.dto.LocationResponseDtoComponent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder @Getter
public class MapResponseComponent {

    private Long id;
    private String category;
    private String kakaoLocationCode;
    private LocationResponseDtoComponent location;
}
