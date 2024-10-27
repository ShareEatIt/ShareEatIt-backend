package com.carpBread.shareEatIt.domain.sharingPost.dto.map;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@AllArgsConstructor
@Builder
public class MapListResponseDto {
    private List<MapResponseComponent> mapList;
}
