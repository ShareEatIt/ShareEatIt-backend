package com.carpBread.shareEatIt.domain.auth;

import com.carpBread.shareEatIt.global.exception.AppException;
import com.carpBread.shareEatIt.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum LoginProvider {
    LOCAL, KAKAO, NAVER, GOOGLE;

    public static LoginProvider toEnum(String name){
        for (LoginProvider type: LoginProvider.values()){
            if (type.name().equals(name)){
                return type;
            }
        }
        throw new AppException(ErrorCode.INVALID_LOGIN_TYPE, "찾을 수 없는 login provider입니다","/signup");
    }
}
