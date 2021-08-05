package com.szmsd.doc.validator;

import com.szmsd.doc.validator.annotation.PreNotNull;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

/**
 * @author zhangyuyuan
 * @date 2021-08-03 17:44
 */
public class PreNotNullValidator implements ConstraintValidator<PreNotNull, Object> {

    private PreNotNull preNotNull;

    @Override
    public void initialize(PreNotNull constraintAnnotation) {
        this.preNotNull = constraintAnnotation;
    }

    /**
     * <blockquote><pre>
     * 级联验证
     * 1.模式：NOT_NULL
     *   如果 field() 字段不为null值，则 linkageFields() 中配置的字段，值都不能为 null
     * 2.模式：VALUE
     *   如果 field() 字段的值与 fieldValue() 配置的值相等，则 linkageFields() 中配置的字段，值都不能为 null
     * </pre></blockquote>
     *
     * @param o                          请求对象
     * @param constraintValidatorContext 当前验证容器
     * @return boolean
     */
    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        if (null == o) {
            return true;
        }
        if (null == this.preNotNull) {
            return true;
        }
        Object value = ObjectUtil.getValue(o, ObjectUtil.convertToGetMethod(this.preNotNull.field()));
        boolean needValid;
        if (PreNotNull.Model.NOT_NULL.equals(this.preNotNull.model())) {
            needValid = Objects.nonNull(value);
        } else {
            needValid = this.preNotNull.fieldValue().equals(String.valueOf(value));
        }
        if (needValid) {
            return ObjectUtil.isAllAnyNull(o, this.preNotNull.linkageFields());
        }
        return true;
    }
}
