package com.carpBread.shareEatIt.domain.participation.dto;

import com.carpBread.shareEatIt.domain.sharingPost.entity.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@NoArgsConstructor
public class ParticipationHistoryResponseDto {
    private Long sharingPostId;
    private String title;
    private PostType provider;
    private String writerName;
    private PostCategory category;
//    private PostImgUrl imgURL;
    private PostStatus status;
    private Date endDate;
    private LocalDateTime createdAt;  // LocalDateTime

//    //이미지와 생성시각을 제외한 dto
//    @Builder
//    public ParticipationHistoryResponseDto(Long sharingPostId, String title, PostType provider, String writerName, PostCategory category,  PostStatus status, Date endDate) {
//        this.sharingPostId = sharingPostId;
//        this.title = title;
//        this.provider = provider;
//        this.writerName = writerName;
//        this.category = category;
////        this.imgURL = imgURL;
//        this.status = status;
//        this.endDate = endDate;
////        this.createdAt = createdAt;
//    }

    //이미지를 제외한 dto
    @Builder
    public ParticipationHistoryResponseDto(Long sharingPostId, String title, PostType provider, String writerName, PostCategory category,  PostStatus status, Date endDate, LocalDateTime createdAt) {
        this.sharingPostId = sharingPostId;
        this.title = title;
        this.provider = provider;
        this.writerName = writerName;
        this.category = category;
//        this.imgURL = imgURL;
        this.status = status;
        this.endDate = endDate;
        this.createdAt = createdAt;
    }

    public static ParticipationHistoryResponseDto from(SharingPost post){
        return new ParticipationHistoryResponseDto(
                post.getId(),
                post.getTitle(),
                post.getPostType(),
                post.getWriter().getNickname(),
                post.getCategory(),
//                post.getPostType(), // image
                post.getStatus(),
                post.getEndDate(),
                post.getCreatedAt()
        );
    }
}
