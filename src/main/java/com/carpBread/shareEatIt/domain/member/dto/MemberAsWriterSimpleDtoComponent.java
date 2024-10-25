package com.carpBread.shareEatIt.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter @Builder
public class MemberAsWriterSimpleDtoComponent {

    private Long id;
    private String img;
    private String nickname;
    private Integer sharingTotal;
}
