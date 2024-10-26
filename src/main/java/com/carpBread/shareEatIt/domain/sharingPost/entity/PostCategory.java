package com.carpBread.shareEatIt.domain.sharingPost.entity;

import com.carpBread.shareEatIt.global.exception.AppException;
import com.carpBread.shareEatIt.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PostCategory {
    BAKERY, BEVERAGE, CONVENIENCE_FOOD, KOREAN, JAPANESE, CHINESE,WESTERN, SNACK, GROCERIES, ETC;

    public static PostCategory toEnumType(String category){
        for (PostCategory enumType : PostCategory.values()){
            if (category.equals(enumType.name())){
                return enumType;
            }
        }
        throw new AppException(ErrorCode.INVALID_ENUM_VALUE, "잘못된 SHARING POST CATEGORY ENUM 값 입니다","/sharing");
    }

}
