package com.carpBread.shareEatIt.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberAsWriterSimpleDtoComponent {

    private Long id;
    private String img;
    private String nickname;
    private Long sharingTotal;

    public MemberAsWriterSimpleDtoComponent(Long id,
                                            String img,
                                            String nickname,
                                            Long sharingTotal) {
        this.id = id;
        this.img = img;
        this.nickname = nickname;
        this.sharingTotal = sharingTotal;
    }
}
