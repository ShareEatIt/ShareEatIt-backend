package com.carpBread.shareEatIt.domain.notice.dto;

import com.carpBread.shareEatIt.domain.notice.entity.NoticeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder @Getter
public class NoticeCreateDto {
    private Long id;
    private String title;
    private String message;
    private NoticeType noticeType;
    private LocalDateTime createdAt;

    private NoticeRelatedObjectResponseComponent noticeObject;


}
