package com.carpBread.shareEatIt.domain.sharingPost.dto.map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Builder @Getter
public class MapListResponseDto {
    private List<MapResponseComponent> mapList;
}
