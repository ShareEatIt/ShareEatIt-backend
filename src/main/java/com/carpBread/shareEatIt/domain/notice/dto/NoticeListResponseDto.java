package com.carpBread.shareEatIt.domain.notice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@AllArgsConstructor
@Builder
public class NoticeListResponseDto {

    private Boolean isRead;
    private List<NoticeResponseComponent> noticeList;
}
