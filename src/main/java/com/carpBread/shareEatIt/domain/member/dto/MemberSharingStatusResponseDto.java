package com.carpBread.shareEatIt.domain.member.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder @Getter
public class MemberSharingStatusResponseDto {

    private MemberCompletedProfileResponseComponent writer;
    private MemberSharingStatusResponseComponent statusByCategory;
}
