package com.carpBread.shareEatIt.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
@Builder
public class MemberCompletedProfileResponseComponent {
    private Long id;
    private String email;
    private String imgUrl;
    private String nickname;
    private Long sharingTotal;
}
