package com.carpBread.shareEatIt.domain.member.entity;

import com.carpBread.shareEatIt.global.exception.CustomException;
import com.carpBread.shareEatIt.global.exception.CustomExceptionStatus;
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
//        throw new CustomException(CustomExceptionStatus.INVALID_ENUM_VALUE_PROVIDER,"MEMBER PROVIDER ENUM 값이 존재하지 않습니다","/members");

        return null;
    }


}
