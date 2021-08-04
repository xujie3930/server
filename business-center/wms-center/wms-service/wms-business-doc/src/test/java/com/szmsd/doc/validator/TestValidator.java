package com.szmsd.doc.validator;

import com.alibaba.fastjson.JSON;
import com.szmsd.doc.api.delivery.request.PricedProductRequest;
import com.szmsd.doc.validator.annotation.NotAnyNull;
import com.szmsd.doc.validator.annotation.PreNotNull;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import javax.validation.metadata.ConstraintDescriptor;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;

/**
 * @author zhangyuyuan
 * @date 2021-07-29 17:17
 */
public class TestValidator {

    @Test
    public void validator() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        PricedProductRequest request = new PricedProductRequest();
        // request.setSkus(new ArrayList<>());
        request.setClientCode("123");
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
                if (StringUtils.isEmpty(property)) {
                    ConstraintDescriptor<?> constraintDescriptor = cv.getConstraintDescriptor();
                    Annotation annotation = constraintDescriptor.getAnnotation();
                    if (annotation instanceof NotAnyNull) {
                        NotAnyNull notAnyNull = (NotAnyNull) annotation;
                        StringJoiner joiner = new StringJoiner(",");
                        String[] fields = notAnyNull.fields();
                        for (String field : fields) {
                            joiner.add(field);
                        }
                        property = joiner.toString();
                    } else if (annotation instanceof PreNotNull) {
                        PreNotNull preNotNull = (PreNotNull) annotation;
                        StringJoiner joiner = new StringJoiner(",");
                        String[] fields = preNotNull.linkageFields();
                        for (String field : fields) {
                            joiner.add(field);
                        }
                        property = joiner.toString();
                    }
                }
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
