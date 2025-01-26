package com.carpBread.shareEatIt.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MemberProfileResponseDto {
    private Long id;
    private String profileImg;
    private String nickname;
    private String email;
    private LocationResponseDtoComponent location;
    private String provider;
    private LocalDateTime joinedAt;
    private LocalDateTime recentModifiedAt;

    public MemberProfileResponseDto(Long id, String profileImg,
                                    String nickname, String email,
                                    LocationResponseDtoComponent location,
                                    String provider, LocalDateTime joinedAt,
                                    LocalDateTime recentModifiedAt) {
        this.id = id;
        this.profileImg = profileImg;
        this.nickname = nickname;
        this.email = email;
        this.location = location;
        this.provider = provider;
        this.joinedAt = joinedAt;
        this.recentModifiedAt = recentModifiedAt;
    }
}
