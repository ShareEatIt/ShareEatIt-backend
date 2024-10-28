package com.carpBread.shareEatIt.domain.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder @Getter
public class ReportPostResponseComponent {
    private Long id;
    private ReportMemberResponseComponent writer;
}
