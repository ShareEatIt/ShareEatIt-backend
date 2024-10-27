package com.carpBread.shareEatIt.domain.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
public class ReportCreateResponseDto {
    private Long id;
    private String title;
    private String content;
    private String imgUrl;
    private ReportMemberResponseComponent reporter;
    private ReportPostResponseComponent post;
    private LocalDateTime createdAt;
    private String status;

}
