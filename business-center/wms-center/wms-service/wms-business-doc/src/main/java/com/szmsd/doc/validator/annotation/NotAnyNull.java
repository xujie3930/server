package com.szmsd.doc.validator.annotation;

import com.szmsd.doc.validator.NotAnyNullValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author zhangyuyuan
 * @date 2021-08-03 16:27
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RUNTIME)
@Documented
@Repeatable(NotAnyNull.List.class)
@Constraint(validatedBy = NotAnyNullValidator.class)
public @interface NotAnyNull {

    // 必须加
    String message() default "{javax.validation.constraints.NotNull.message}";

    // 必须加
    Class<?>[] groups() default {};

    // 必须加
    Class<? extends Payload>[] payload() default {};

    /**
     * 需要验证的字段
     * <p>
     * 验证条件：配置的所有字段中，不能全部都是null值
     *
     * @return 字段名称
     */
    String[] fields() default {};

    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface List {
        NotAnyNull[] value();
    }
}
