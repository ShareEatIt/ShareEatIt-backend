package com.carpBread.shareEatIt.domain.sharingPost.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@AllArgsConstructor
@Builder
public class SharingPostListResponseDto {

    private String provider;
    private List<SharingPostSimpleResponseComponent> postList;

}
