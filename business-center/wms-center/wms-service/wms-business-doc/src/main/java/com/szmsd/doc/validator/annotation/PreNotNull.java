package com.szmsd.doc.validator.annotation;

import com.szmsd.doc.validator.PreNotNullValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author zhangyuyuan
 * @date 2021-08-03 17:40
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RUNTIME)
@Documented
@Repeatable(PreNotNull.List.class)
@Constraint(validatedBy = PreNotNullValidator.class)
public @interface PreNotNull {

    // 必须加
    String message() default "{javax.validation.constraints.NotNull.message}";

    // 必须加
    Class<?>[] groups() default {};

    // 必须加
    Class<? extends Payload>[] payload() default {};

    /**
     * 前置字段
     *
     * @return String
     */
    String field();

    /**
     * 需要验证的字段
     *
     * @return String
     */
    String[] linkageFields();

    /**
     * 验证模式
     *
     * @return Model
     */
    Model model() default Model.NOT_NULL;

    /**
     * 前置字段值
     *
     * @return String
     */
    String fieldValue() default "";

    /**
     * 验证模式
     */
    enum Model {
        /**
         * 当值不为空时
         */
        NOT_NULL,
        /**
         * 当字段为某个值时
         */
        VALUE
    }

    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface List {
        PreNotNull[] value();
    }
}
