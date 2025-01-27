package com.carpBread.shareEatIt.domain.auth;

import com.carpBread.shareEatIt.global.exception.CustomException;
import com.carpBread.shareEatIt.global.exception.CustomExceptionStatus;
import com.carpBread.shareEatIt.global.exception.Domain;
import lombok.Getter;

/* 로그인 유형 - 자체 로그인, 소셜 oauth2 로그인 (카카오, 네이버, 구글) */
@Getter
public enum LoginProvider {
    LOCAL("local"), KAKAO("kakao"), NAVER("naver"), GOOGLE("google");
    private String registrationId;

    LoginProvider(String registrationId) {
        this.registrationId = registrationId;
    }

    public static LoginProvider toEnum(String name){
        for (LoginProvider type: LoginProvider.values()){
            if (type.name().equals(name) || type.getRegistrationId().equals(name)){
                return type;
            }
        }
        throw new CustomException(
                CustomExceptionStatus.INVALID_LOGIN_TYPE,
                "서버에서 제공하지 않는 로그인 경로(Login provider)입니다.",
                LoginProvider.class.getName(),
                name,
                Domain.AUTH);
    }

}
