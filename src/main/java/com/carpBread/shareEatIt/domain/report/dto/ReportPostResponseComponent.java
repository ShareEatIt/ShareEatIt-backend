package com.carpBread.shareEatIt.domain.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class ReportPostResponseComponent {
    private Long id;
    private ReportMemberResponseComponent writer;
}
