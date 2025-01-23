package com.carpBread.shareEatIt.domain.member.annotation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.springframework.data.mongodb.core.mapping.Document;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 회원가입 시 PASSWORD가 정해진 문자열 패턴에 맞는지 여부 판단하는 CUSTOM ANNOTATION
 */
@Document
@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {

    // 에러 시 반환하는 에러 메세지
    String message() default "Given password is not allow on this application password pattern";

    // 특정 상황에서 검증 로직을 실행하도록 group 설정
    Class<?>[] groups() default {};

    // 검증 시 에러 결과에 메타정보 추가
    Class<? extends Payload>[] payload() default {};

}
