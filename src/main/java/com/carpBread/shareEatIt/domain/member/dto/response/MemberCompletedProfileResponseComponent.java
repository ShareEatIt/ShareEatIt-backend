package com.carpBread.shareEatIt.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberCompletedProfileResponseComponent {
    private Long id;
    private String email;
    private String imgUrl;
    private String nickname;
    private Long sharingTotal;

    public MemberCompletedProfileResponseComponent(Long id,
                                                   String email, String imgUrl, String nickname, Long sharingTotal) {
        this.id = id;
        this.email = email;
        this.imgUrl = imgUrl;
        this.nickname = nickname;
        this.sharingTotal = sharingTotal;
    }
}
