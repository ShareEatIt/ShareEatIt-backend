package com.carpBread.shareEatIt.domain.auth.dto;

import com.carpBread.shareEatIt.domain.member.entity.Member;
import com.carpBread.shareEatIt.domain.member.entity.Provider;
import com.carpBread.shareEatIt.global.exception.AppException;
import com.carpBread.shareEatIt.global.exception.ErrorCode;
import lombok.Builder;

import java.util.Map;

@Builder
public record OAuth2UserInfo(
        Long id,
        String nickname,
        String email,
        String image

) {
    /* 여러 서버 구별 */
    public static OAuth2UserInfo of(String registrationId, Map<String, Object> attributes){
        return switch (registrationId){
            case "kakao" -> ofKakao(attributes);
            default -> throw new AppException(ErrorCode.NOT_FOUND_OAUTH2_REGISTRATION_ID,"제공하지 않는 OAUTH2 서버입니다","/oauth2/authorize");
        };

    }

    private static OAuth2UserInfo ofKakao(Map<String, Object> attributes){
        Long id = (Long) attributes.get("id");
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) account.get("profile");

        return OAuth2UserInfo.builder()
                .id(id)
                .nickname((String) profile.get("nickname"))
                .email((String) account.get("email"))
                .image((String) profile.get("profile_image_url"))
                .build();
    }

    public Member toEntity(String accessToken, String refreshToken){
        return Member.builder()
                .accessId(this.id)
                .email(this.email)
                .nickname(this.nickname)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .profileImgUrl(this.image)
                .provider(Provider.INDIVIDUAL)
                .isKeywordAvail(true)
                .isNoticeAvail(true)
                .build();

    }



}
