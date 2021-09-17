package com.szmsd.doc.validator;

import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.doc.validator.annotation.Dictionary;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DictionaryValidator implements ConstraintValidator<Dictionary, Object> {

    private Dictionary dictionary;

    @Override
    public void initialize(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        if (null == this.dictionary) {
            return true;
        }
        if (null == o) {
            return true;
        }
        String type = this.dictionary.type();
        Object bean = SpringUtils.getBean(type);
        DictionaryPlugin plugin = (DictionaryPlugin) bean;
        return plugin.valid(o, this.dictionary.param());
    }
}
