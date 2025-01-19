package com.carpBread.shareEatIt.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter @Builder
@AllArgsConstructor
public class MemberProfileResponseDto {
    private Long id;
    private String profileImg;
    private String nickname;
    private String email;
    private LocationResponseDtoComponent location;
    private String provider;
    private LocalDateTime joinedAt;
    private LocalDateTime recentModifiedAt;

}
