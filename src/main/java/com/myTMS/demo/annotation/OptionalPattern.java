package com.myTMS.demo.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Bean Validation 구현체를 스캔하는 @Constraint 어노테이션에서 사용할 커스텀 패턴 검증 클래스를 정의해서 사용
 * @Target : 적용 범위를 설정
 * @Retention : 런타임 시점까지 유지되는 어노테이션임을 설정 (사실상 어플리케이션 종료 시점까지 유지됨)
 * @Constraint : 이 어노테이션이 어떤 검증기를 사용하여 검증하는지를 결정
 */

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = OptionalPatternValidator.class)
public @interface OptionalPattern {

    String message() default "형식이 잘못되었습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String regexp();
}
