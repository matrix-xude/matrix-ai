package com.matrix.ai.validation;

import com.matrix.ai.enums.OperatorType;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.annotation.*;

/**
 * 有效的运算符校验注解
 */
@Documented
@Constraint(validatedBy = ValidOperator.Validator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidOperator {

    String message() default "无效的运算符，支持的运算符：add, sub, mul, div";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<ValidOperator, OperatorType> {
        @Override
        public boolean isValid(OperatorType value, ConstraintValidatorContext context) {
            return value != null;
        }
    }
}
