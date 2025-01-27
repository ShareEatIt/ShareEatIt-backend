package com.carpBread.shareEatIt.domain.member.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class PasswordValidator implements ConstraintValidator<Password, String> {

    // password 문자열 최소 길이
    private static final int MIN_SIZE = 8;
    // password 문자열 최대 길이
    private static final int MAX_SIZE= 25;

    // 문자열 정규 표현식 지정 : 영문자, 숫자, 특수문자 1개 이상 포함하는 8 이상 25 이하 길이의 문자열
    private static final String regexPassword = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@!%*#?&])[A-Za-z[0-9]$@!%*#?&]{"
            +MIN_SIZE+","
            +MAX_SIZE+"}$";

    // Pattern 객체
    private static final Pattern PATTERN = Pattern.compile(regexPassword);

    @Override
    public void initialize(Password constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    // pattern에 맞는지 여부를 반환
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        return isMatches(password);
    }

    // 내부 matches isMatch
    private boolean isMatches(String password){
        return PATTERN.matcher(password).matches();
    }
}
