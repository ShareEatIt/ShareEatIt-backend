package com.carpBread.shareEatIt.domain.sharingPost.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder @Getter
public class SharingPostListResponseDto {

    private String provider;
    private List<SharingPostSimpleResponseComponent> postList;


}
