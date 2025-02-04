package com.carpBread.shareEatIt.domain.member.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberSharingStatusResponseDto {

    private MemberCompletedProfileResponseComponent writer;
    private MemberSharingStatusResponseComponent statusByCategory;

    public MemberSharingStatusResponseDto(MemberCompletedProfileResponseComponent writer,
                                          MemberSharingStatusResponseComponent statusByCategory) {
        this.writer = writer;
        this.statusByCategory = statusByCategory;
    }
}
