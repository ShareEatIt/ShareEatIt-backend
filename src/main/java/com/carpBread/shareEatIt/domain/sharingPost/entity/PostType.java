package com.carpBread.shareEatIt.domain.sharingPost.entity;

import com.carpBread.shareEatIt.global.exception.AppException;
import com.carpBread.shareEatIt.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PostType {
    STORE, INDIVIDUAL;

    public static PostType toEnumType(String postType){
        for (PostType enumType : PostType.values()){
            if (postType.equals(enumType.name())){
                return enumType;
            }
        }
        throw new AppException(ErrorCode.INVALID_ENUM_VALUE, "잘못된 SHARING POST TYPE ENUM 값 입니다","/sharing");
    }
}