package com.carpBread.shareEatIt.domain.auth;

import com.carpBread.shareEatIt.global.exception.CustomException;
import com.carpBread.shareEatIt.global.exception.CustomExceptionStatus;
import lombok.Getter;

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
//        throw new CustomException(CustomExceptionStatus.INVALID_LOGIN_TYPE, "찾을 수 없는 login provider입니다","/signup");
        return null;
    }

}
