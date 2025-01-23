package com.carpBread.shareEatIt.domain.sharingPost.entity;

import com.carpBread.shareEatIt.global.exception.CustomException;
import com.carpBread.shareEatIt.global.exception.CustomExceptionStatus;
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
        /* throw new CustomException(CustomExceptionStatus.INVALID_ENUM_VALUE, "잘못된 SHARING POST TYPE ENUM 값 입니다","/sharing")*/;
        return null;
    }
}