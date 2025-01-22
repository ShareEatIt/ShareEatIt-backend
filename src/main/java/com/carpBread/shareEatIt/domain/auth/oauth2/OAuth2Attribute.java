package com.carpBread.shareEatIt.domain.auth.oauth2;

import com.carpBread.shareEatIt.domain.auth.LoginProvider;
import com.carpBread.shareEatIt.global.exception.CustomException;
import com.carpBread.shareEatIt.global.exception.CustomExceptionStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/* oauth2 로그인 사용자의 정보를 추출 */
@Builder(access = AccessLevel.PRIVATE)
@Getter
public class OAuth2Attribute {
    private Map<String, Object> attributes;
    private LoginProvider provider;
    private String nameAttributeKey;
    private String email;
    private String nickname;
    private String profileImage;

    // provider 구분 및 oauth2attribute 객체 반환
    public static OAuth2Attribute of(String provider, String nameAttributeKey,
                              Map<String, Object> attributes){
        switch (provider){
            case "kakao":
                return ofKakao(nameAttributeKey, attributes);
            case "naver":
                return ofNaver(nameAttributeKey, attributes);
            case "google":
                return ofGoogle(nameAttributeKey, attributes);
            default:
                throw new CustomException(CustomExceptionStatus.NOT_FOUND_MEMBER, "","");
        }
    }

    // google 로그인 시 사용자 정보 추출
    private static OAuth2Attribute ofGoogle(String nameAttributeKey,
                                            Map<String, Object> attributes) {
        return OAuth2Attribute.builder()
                .nickname((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .profileImage((String) attributes.get("picture"))
                .attributes(attributes)
                .provider(LoginProvider.GOOGLE)
                .nameAttributeKey(nameAttributeKey)
                .build();
    }

    // kakao 로그인 시 사용자 정보 추출
    private static OAuth2Attribute ofKakao(String nameAttributeKey,
                                           Map<String, Object> attributes){

        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");


        return OAuth2Attribute.builder()
                .nickname((String) kakaoProfile.get("nickname"))
                .profileImage((String) kakaoProfile.get("profile"))
                .email((String) kakaoAccount.get("email"))
                .provider(LoginProvider.KAKAO)
                .nameAttributeKey(nameAttributeKey)
                .attributes(kakaoAccount)
                .build();
    }

    // naver 로그인 시 사용자 정보 추출
    private static OAuth2Attribute ofNaver(String nameAttributeKey,
                                           Map<String, Object> attributes){
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuth2Attribute.builder()
                .nickname((String) response.get("nickname"))
                .profileImage((String) response.get("profile_image"))
                .email((String) response.get("email"))
                .provider(LoginProvider.NAVER)
                .nameAttributeKey(nameAttributeKey)
                .attributes(response)
                .build();
    }

    // OAuth2User 객체에 담을 수 있게 정보를 map 에 담기
    public Map<String, Object> convertToMap(){
        Map<String , Object> userAttributeMap = new HashMap<>();
        userAttributeMap.put("provider", this.provider);
        userAttributeMap.put("name_attribute_key", this.nameAttributeKey);
        userAttributeMap.put("email", this.email);
        userAttributeMap.put("nickname", this.nickname);
        userAttributeMap.put("profile_image", this.profileImage);
        userAttributeMap.put("attributes", this.attributes);

        return userAttributeMap;
    }

}


