package com.carpBread.shareEatIt.domain.member.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class MemberSharingStatusResponseDto {

    private MemberCompletedProfileResponseComponent writer;
    private MemberSharingStatusResponseComponent status;
}
