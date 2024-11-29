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
    private String firstImgUrl;
    private PostStatus status;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;  // LocalDateTime

    public ParticipationHistoryResponseDto(Long sharingPostId, String title, PostType provider, String writerName, PostCategory category, String firstImgUrl, PostStatus status, LocalDateTime endDate, LocalDateTime createdAt) {
        this.sharingPostId = sharingPostId;
        this.title = title;
        this.provider = provider;
        this.writerName = writerName;
        this.category = category;
        this.firstImgUrl = firstImgUrl;
        this.status = status;
        this.endDate = endDate;
        this.createdAt = createdAt;
    }

    @Builder
    public static ParticipationHistoryResponseDto from(SharingPost post, String firstImgUrl){
        return new ParticipationHistoryResponseDto(
                post.getId(),
                post.getTitle(),
                post.getPostType(),
                post.getWriter().getNickname(),
                post.getCategory(),
                firstImgUrl, // image
                post.getStatus(),
                post.getEndAt(),
                post.getCreatedAt()
        );
    }
}
