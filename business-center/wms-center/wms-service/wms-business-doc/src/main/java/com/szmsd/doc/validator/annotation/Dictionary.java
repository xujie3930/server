package com.szmsd.doc.validator.annotation;

import com.szmsd.doc.validator.DictionaryValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = DictionaryValidator.class)
public @interface Dictionary {

    // 必须加
    String message() default "{javax.validation.constraints.NotNull.message}";

    // 必须加
    Class<?>[] groups() default {};

    // 必须加
    Class<? extends Payload>[] payload() default {};

    /**
     * 类型
     */
    String type();

    /**
     * 参数
     */
    String param() default "";
}
