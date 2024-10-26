package com.carpBread.shareEatIt.domain.sharingPost.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
public class SharingPostSimpleResponseComponent {

    private Long id;
    private LocalDateTime createdAt;
    private String title;
    private LocalDateTime endAt;
    private String nickname;
    private String category;
    private Integer dDay;
    private String ago;
    private String img;

}
