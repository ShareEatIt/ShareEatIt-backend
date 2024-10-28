package com.carpBread.shareEatIt.domain.notice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder @Getter
public class NoticeResponseComponent {
    private Long id;
    private String title;
    private String message;
}
