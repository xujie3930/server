package com.szmsd.doc.validator;

import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.doc.validator.annotation.Dictionary;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collection;

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
        boolean required = dictionary.required();
        if (!required) {
            // 目前支持的数据类型
            if (o instanceof String && StringUtils.isEmpty((String) o)) {
                return true;
            } else if (o instanceof Collection && CollectionUtils.isEmpty((Collection<?>) o)) {
                return true;
            }
        }
        String type = this.dictionary.type();
        Object bean = SpringUtils.getBean(type);
        DictionaryPlugin plugin = (DictionaryPlugin) bean;
        return plugin.valid(o, this.dictionary.param());
    }
}
