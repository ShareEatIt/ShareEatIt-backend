package com.carpBread.shareEatIt.domain.sharingPost.dto.response;

import com.carpBread.shareEatIt.domain.sharingPost.entity.SharingPost;
import lombok.*;

import java.time.LocalDateTime;


@Getter
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

    public SharingPostSimpleResponseComponent(Long id, LocalDateTime createdAt,
                                              String title, LocalDateTime endAt,
                                              String nickname, String category,
                                              Integer dDay, String ago, String img) {
        this.id = id;
        this.createdAt = createdAt;
        this.title = title;
        this.endAt = endAt;
        this.nickname = nickname;
        this.category = category;
        this.dDay = dDay;
        this.ago = ago;
        this.img = img;
    }
}
