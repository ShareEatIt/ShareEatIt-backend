package com.carpBread.shareEatIt.domain.report.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class ReportCreateRequestDto {
    @NotNull
    private Long postId;
    @NotNull
    private String title;
    @NotNull
    private String content;

    public ReportCreateRequestDto(Long postId, String title, String content) {
        this.postId = postId;
        this.title = title;
        this.content = content;
    }
}
