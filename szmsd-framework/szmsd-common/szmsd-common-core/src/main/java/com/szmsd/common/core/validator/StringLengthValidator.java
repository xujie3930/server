package com.szmsd.common.core.validator;

import com.szmsd.common.core.validator.annotation.StringLength;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class StringLengthValidator implements ConstraintValidator<StringLength, Object> {

    private StringLength stringLength;

    @Override
    public void initialize(StringLength constraintAnnotation) {
        this.stringLength = constraintAnnotation;
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (Objects.isNull(value)) {
            return true;
        }
        if (null != stringLength) {
            int length = String.valueOf(value).length();
            int maxLen = stringLength.maxLength(),
                    minLen = stringLength.minLength();
            return length >= minLen && length <= maxLen;
        }
        return false;
    }
}
