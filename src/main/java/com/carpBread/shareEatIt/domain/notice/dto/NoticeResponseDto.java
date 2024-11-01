package com.carpBread.shareEatIt.domain.notice.dto;

import com.carpBread.shareEatIt.domain.member.dto.MemberAsWriterSimpleDtoComponent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder @Getter
public class NoticeResponseDto {

    private Long id;
    private String noticeType;
    private String title;
    private String message;
    private MemberAsWriterSimpleDtoComponent member;

}
