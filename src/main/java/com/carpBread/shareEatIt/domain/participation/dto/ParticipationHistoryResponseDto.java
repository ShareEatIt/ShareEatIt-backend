package com.carpBread.shareEatIt.domain.participation.dto;

import com.carpBread.shareEatIt.domain.sharingPost.entity.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@NoArgsConstructor
public class ParticipationHistoryResponseDto {
    private Long sharingPostId;
    private String title;
    private PostType provider;
    private String writerName;
    private PostCategory category;
    private PostImgUrl imgUrl;
    private PostStatus status;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;  // LocalDateTime

    public ParticipationHistoryResponseDto(Long sharingPostId, String title, PostType provider, String writerName, PostCategory category, PostImgUrl imgUrl, PostStatus status, LocalDateTime endDate, LocalDateTime createdAt) {
        this.sharingPostId = sharingPostId;
        this.title = title;
        this.provider = provider;
        this.writerName = writerName;
        this.category = category;
        this.imgUrl = imgUrl;
        this.status = status;
        this.endDate = endDate;
        this.createdAt = createdAt;
    }

    @Builder
    public static ParticipationHistoryResponseDto from(SharingPost post){
        return new ParticipationHistoryResponseDto(
                post.getId(),
                post.getTitle(),
                post.getPostType(),
                post.getWriter().getNickname(),
                post.getCategory(),
                post.getPostImgUrlList().get(0), // image
                post.getStatus(),
                post.getEndAt(),
                post.getCreatedAt()
        );
    }
}
