package com.carpBread.shareEatIt.domain.auth.dto;

import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.entity.Provider;
import com.carpBread.shareEatIt.global.exception.CustomException;
import com.carpBread.shareEatIt.global.exception.CustomExceptionStatus;
import com.carpBread.shareEatIt.global.exception.Domain;
import lombok.Builder;
import lombok.Getter;
import org.locationtech.jts.geom.Point;

import java.util.Map;

/* 수정 중입니다. 삭제 예정 */
public record Pre_OAuth2UserInfo(
        Long id,
        String nickname,
        String email,
        String image

) {
    /* 여러 서버 구별 */
    public static Pre_OAuth2UserInfo of(String registrationId, Map<String, Object> attributes){
        return switch (registrationId){
            case "kakao" -> ofKakao(attributes);
            default -> throw new CustomException(CustomExceptionStatus.NOT_FOUND_OAUTH2_REGISTRATION_ID,
                    "제공하지 않는 소셜 로그인입니다.",
                    Pre_OAuth2UserInfo.class.getName(),
                    registrationId,
                    Domain.AUTH);
            };
    }

    private static Pre_OAuth2UserInfo ofKakao(Map<String, Object> attributes){
        Long id = (Long) attributes.get("id");
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) account.get("profile");

        return new Pre_OAuth2UserInfo(id,
                (String) profile.get("nickname"),
                (String) account.get("email"),
                (String) profile.get("profile_image_url"));

    }

    public Member toEntity(String accessToken, String refreshToken, Point point){


        return Member.builder()
                .email(this.email)
                .nickname(this.nickname)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .locationPoint(point)
                .profileImgUrl(this.image)
                .provider(Provider.INDIVIDUAL)
                .isKeywordAvail(true)
                .isNoticeAvail(true)
                .build();

    }



}
