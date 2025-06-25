package com.myTMS.demo.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Bean Validation 에 등록한 커스텀 검증기 클래스
 * 제네릭 타입 선언을 한 ConstraintValidator 인터페이스를 구현하여 커스텀 어노테이션을 등록할 수 있음
 * 타입 매개변수는 어노테이션의 자식 클래스와 검증할 클래스 타입을 지정하면 됨
 * initialize() 메서드는 어노테이션이 지정한 메타데이터를 가져와 Pattern 객체를 지정하고 compile 하여 정규표현식을 설정함
 * isValid() 메서드는 실제 검증 로직을 구현하는 메서드로 어노테이션이 부착된 필드 (OptionalPattern 에서 지정한 값은 Field기 때문에 Field 로 한정함)
 * 값을 검증한다 이 로직에서는 null 이거나 비어있는 경우에는 그냥 true 를 리턴하여 검증을 통과하도록 함
 */

public class OptionalPatternValidator implements ConstraintValidator<OptionalPattern, String> {

    private Pattern pattern;

    @Override
    public void initialize(OptionalPattern constraintAnnotation) {
        this.pattern = Pattern.compile(constraintAnnotation.regexp());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }
        return pattern.matcher(value).matches();
    }
}
