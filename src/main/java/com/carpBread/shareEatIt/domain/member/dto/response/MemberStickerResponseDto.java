package com.carpBread.shareEatIt.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberStickerResponseDto {
    private Long id;
    private String profileImg;
    private String nickname;
    private String email;
    private StickersResponseDto stickers;
    private Boolean isNoticeAvail;
    private Boolean isKeywordAvail;
    private String provider;

    public MemberStickerResponseDto(Long id, String profileImg,
                                    String nickname, String email,
                                    StickersResponseDto stickers,
                                    Boolean isNoticeAvail,
                                    Boolean isKeywordAvail,
                                    String provider) {
        this.id = id;
        this.profileImg = profileImg;
        this.nickname = nickname;
        this.email = email;
        this.stickers = stickers;
        this.isNoticeAvail = isNoticeAvail;
        this.isKeywordAvail = isKeywordAvail;
        this.provider = provider;
    }
}
