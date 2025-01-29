package com.carpBread.shareEatIt.domain.member.entity;

import com.carpBread.shareEatIt.global.exception.CustomException;
import com.carpBread.shareEatIt.global.exception.CustomExceptionStatus;
import com.carpBread.shareEatIt.global.exception.Domain;
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
        throw new CustomException(
                CustomExceptionStatus.INVALID_ENUM_VALUE,
                "존재하지 않는 Provider 값입니다",
                Provider.class.getSimpleName(),
                value,
                Domain.MEMBER
        );
    }


}
