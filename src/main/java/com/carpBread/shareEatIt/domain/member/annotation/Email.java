package com.carpBread.shareEatIt.domain.member.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.springframework.data.mongodb.core.mapping.Document;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 사용자가 입력한 email 형식의 정규 표현 충족 여부 확인하는 CUSTOM ANNOTATION
 */
@Document
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = CustomEmailValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface Email {
    // 에러 시 반환하는 에러 메세지
    String message() default "Given email is not allow on this application email pattern";

    // 특정 상황에서 검증 로직을 실행하도록 group 설정
    Class<?>[] groups() default {};

    // 검증 시 에러 결과에 메타정보 추가
    Class<? extends Payload>[] payload() default {};
}
