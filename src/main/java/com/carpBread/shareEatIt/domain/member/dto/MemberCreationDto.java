package com.carpBread.shareEatIt.domain.member.dto;

import com.carpBread.shareEatIt.domain.member.dto.request.SignUpRequestDto;
import lombok.Getter;
import org.locationtech.jts.geom.Point;


@Getter
public class MemberCreationDto {
    private SignUpRequestDto requestDto;
    private Point locationPoint;
    private String encodedPW;
    private String profileImg;

    public MemberCreationDto(SignUpRequestDto dto, Point locationPoint, String encodedPW, String profileImg) {
        this.requestDto = dto;
        this.locationPoint = locationPoint;
        this.encodedPW = encodedPW;
        this.profileImg=profileImg;
    }
}
