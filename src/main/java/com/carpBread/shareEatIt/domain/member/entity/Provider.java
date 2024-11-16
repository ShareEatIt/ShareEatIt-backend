package com.carpBread.shareEatIt.domain.member.entity;

import com.carpBread.shareEatIt.global.exception.AppException;
import com.carpBread.shareEatIt.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Provider {
    STORE, INDIVIDUAL;

    public static Provider toEnum(String value){
        for (Provider provider:Provider.values()){
            if (value.equals(provider.name()))
                return provider;
        }
        throw new AppException(ErrorCode.INVALID_ENUM_VALUE_PROVIDER,"MEMBER PROVIDER ENUM 값이 존재하지 않습니다","/members");
    }


}
