package com.carpBread.shareEatIt.domain.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReportPostResponseComponent {
    private Long id;
    private ReportMemberResponseComponent writer;

    public ReportPostResponseComponent(Long id, ReportMemberResponseComponent writer) {
        this.id = id;
        this.writer = writer;
    }
}
