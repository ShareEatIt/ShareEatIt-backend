package com.carpBread.shareEatIt.domain.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReportCreateResponseDto {
    private Long id;
    private String title;
    private String content;
    private String imgUrl;
    private ReportMemberResponseComponent reporter;
    private ReportPostResponseComponent post;
    private LocalDateTime createdAt;
    private String status;

    public ReportCreateResponseDto(Long id, String title,
                                   String content, String imgUrl,
                                   ReportMemberResponseComponent reporter,
                                   ReportPostResponseComponent post,
                                   LocalDateTime createdAt, String status) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.imgUrl = imgUrl;
        this.reporter = reporter;
        this.post = post;
        this.createdAt = createdAt;
        this.status = status;
    }
}
