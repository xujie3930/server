package com.szmsd.doc.validator;

import com.szmsd.doc.validator.annotation.NotAnyNull;
import org.apache.commons.lang3.ArrayUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author zhangyuyuan
 * @date 2021-08-03 16:28
 */
public class NotAnyNullValidator implements ConstraintValidator<NotAnyNull, Object> {

    private NotAnyNull notAnyNull;

    @Override
    public void initialize(NotAnyNull constraintAnnotation) {
        this.notAnyNull = constraintAnnotation;
    }

    /**
     * 验证字段是否全部为空
     * 例子：有字段code，name。验证需要code或name必选填写一个，注解上字段填写 fields = {"code", "name"}
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
        if (null == this.notAnyNull) {
            return true;
        }
        String[] fields = this.notAnyNull.fields();
        if (ArrayUtils.isEmpty(fields) || fields.length == 1) {
            return true;
        }
        return ObjectUtil.isAnyNull(o, fields);
    }

}
