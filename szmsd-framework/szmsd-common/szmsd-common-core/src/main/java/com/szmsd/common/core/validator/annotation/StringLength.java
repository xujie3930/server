package com.szmsd.common.core.validator.annotation;

import com.szmsd.common.core.validator.StringLengthValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = StringLengthValidator.class)
public @interface StringLength {

    // 必须加
    String message() default "{javax.validation.constraints.NotNull.message}";

    // 必须加
    Class<?>[] groups() default {};

    // 必须加
    Class<? extends Payload>[] payload() default {};

    int minLength() default 0;

    int maxLength() default 16;
}
