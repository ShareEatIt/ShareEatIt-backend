package com.carpBread.shareEatIt.domain.member.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class CustomEmailValidator implements ConstraintValidator<Email, String> {
    // 이메일 문자열 정규 표현식 지정
    private static final String regexEmail="^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    // Pattern 객체
    private static final Pattern PATTERN = Pattern.compile(regexEmail);

    @Override
    public void initialize(Email constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    // regexEmail pattern에 맞는지 여부를 반환
    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        // null 값 허용
        if (email==null) return true;

        return PATTERN.matcher(email).matches();
    }
}
