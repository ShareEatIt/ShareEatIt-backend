package com.carpBread.shareEatIt.domain.sharingPost.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@NoArgsConstructor
@Getter
public class SharingPostListResponseDto {

    private String provider;
    private List<SharingPostSimpleResponseComponent> postList;

    public SharingPostListResponseDto(String provider, List<SharingPostSimpleResponseComponent> postList) {
        this.provider = provider;
        this.postList = postList;
    }
}
