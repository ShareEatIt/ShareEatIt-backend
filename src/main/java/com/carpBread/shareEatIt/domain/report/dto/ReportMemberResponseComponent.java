package com.carpBread.shareEatIt.domain.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReportMemberResponseComponent {

    private Long id;
    private String nickname;

    public ReportMemberResponseComponent(Long id, String nickname) {
        this.id = id;
        this.nickname = nickname;
    }
}
