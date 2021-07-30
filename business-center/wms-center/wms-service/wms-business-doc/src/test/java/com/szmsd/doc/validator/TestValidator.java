package com.szmsd.doc.validator;

import com.alibaba.fastjson.JSON;
import com.szmsd.doc.api.delivery.request.PricedProductRequest;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author zhangyuyuan
 * @date 2021-07-29 17:17
 */
public class TestValidator {

    @Test
    public void validator() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        PricedProductRequest request = new PricedProductRequest();
        System.out.println(JSON.toJSONString(validate(request, validator)));
    }

    public <T> Map<String, StringBuffer> validate(T obj, Validator validator) {
        Map<String, StringBuffer> errorMap = null;
        Set<ConstraintViolation<T>> set = validator.validate(obj, Default.class);
        if (set != null && set.size() > 0) {
            errorMap = new HashMap<>();
            String property;
            for (ConstraintViolation<T> cv : set) {
                //这里循环获取错误信息，可以自定义格式
                property = cv.getPropertyPath().toString();
                if (errorMap.get(property) != null) {
                    errorMap.get(property).append(",").append(cv.getMessage());
                } else {
                    StringBuffer sb = new StringBuffer();
                    sb.append(cv.getMessage());
                    errorMap.put(property, sb);
                }
            }
        }
        return errorMap;
    }
}
