package com.carpBread.shareEatIt.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder @Getter
public class MemberCompletedProfileResponseComponent {
    private Long id;
    private String email;
    private String imgUrl;
    private String nickname;
    private Long sharingTotal;
}
