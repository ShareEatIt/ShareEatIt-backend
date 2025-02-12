package com.carpBread.shareEatIt.domain.participation.dto.responseDto;

import com.carpBread.shareEatIt.domain.sharingPost.entity.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(force = true)
public class ParticipationHistoryResponseDto {
    private final Long sharingPostId;
    private final String title;
    private final PostType provider;
    private final String writerName;
    private final PostCategory category;
    private final String firstImgUrl;
    private final PostStatus status;
    private final LocalDateTime endDate;
    private final LocalDateTime createdAt;

    @Builder
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

    public static ParticipationHistoryResponseDto from(SharingPost post, String firstImgUrl){
        return ParticipationHistoryResponseDto.builder()
                .sharingPostId(post.getId())
                .title(post.getTitle())
                .provider(post.getPostType())
                .writerName(post.getWriter().getNickname())
                .category(post.getCategory())
                .firstImgUrl(firstImgUrl)
                .status(post.getStatus())
                .endDate(post.getEndAt())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
