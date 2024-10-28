package com.carpBread.shareEatIt.domain.notice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Builder @Getter
public class NoticeListResponseDto {

    private Boolean isRead;
    private List<NoticeResponseComponent> noticeList;
}
