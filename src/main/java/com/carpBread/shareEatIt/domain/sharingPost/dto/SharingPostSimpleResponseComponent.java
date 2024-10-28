package com.carpBread.shareEatIt.domain.sharingPost.dto;

import com.carpBread.shareEatIt.domain.sharingPost.entity.SharingPost;
import lombok.*;

import java.time.LocalDateTime;


@Builder @Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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

    public static SharingPostSimpleResponseComponent of(SharingPost post, int dDay, String ago, String firstImgUrl){
        return SharingPostSimpleResponseComponent.builder()
                .id(post.getId())
                .createdAt(post.getCreatedAt())
                .title(post.getTitle())
                .endAt(post.getEndAt())
                .nickname(post.getWriter().getNickname())
                .category(post.getCategory().name())
                .dDay(dDay)
                .ago(ago)
                .img(firstImgUrl)
                .build();

    }

}
