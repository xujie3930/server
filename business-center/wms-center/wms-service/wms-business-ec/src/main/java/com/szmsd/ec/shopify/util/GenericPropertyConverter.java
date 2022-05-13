package com.szmsd.ec.shopify.util;

import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ClassUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * @FileName GenericPropertyConverter.java
 * @Description ----------实体转换---------
 * @Date 2021-04-15 9:35
 * @Author hyx
 * @Version 1.0
 */
public class GenericPropertyConverter {
    public static final char UNDERLINE_CHAR = '_';

    public static <T> T convertObject(Object originObj, Boolean originStyle, Class<T> targetClass) {

        return baseConvertObject(originObj, originStyle, targetClass, null, null);
    }

    public static <T, F> List<T> convertObject(List<F> originLst, Boolean originStyle, Class<T> targetClass) {
        if (Objects.isNull(originLst)) {
            return null;
        }
        List<T> result = Lists.newArrayListWithCapacity(originLst.size());
        originLst.forEach(o -> result.add(convertObject(o, originStyle, targetClass)));
        return result;
    }

    /**
     *
     * @param originObj 待转换的源对象，【不可为null】
     * @param originStyle 源对象的property的命名风格，【不可为null】
     * @param targetClass 目标对象，【不可为null】FieldNamingPolicy
     * @param customizePropertyNameMap 自定义转换属性，【可为null】
     * @param excludePropertyNameList 自定义的排除属性，【可为null】
     * @return
     */
    public static <T> T baseConvertObject(Object originObj, Boolean originStyle, Class<T> targetClass,
                                          Map<String, String> customizePropertyNameMap, List<String> excludePropertyNameList) {

        T targetObj;
        try {
            targetObj = targetClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {

            return null;
        }

        Class<?> originClass = originObj.getClass();
        PropertyDescriptor[] originPds = BeanUtils.getPropertyDescriptors(originClass),
                targetPds = BeanUtils.getPropertyDescriptors(targetClass);
        Function<String, String> propertyConvertFunc = originStyle
                ? GenericPropertyConverter::camel2Underline
                : GenericPropertyConverter::underline2Camel;
        for (PropertyDescriptor originPd : originPds) {

            String propertyName = originPd.getName();
            if ("class".equals(propertyName)) {

                continue;
            }

            if (CollectionUtils.isNotEmpty(excludePropertyNameList) && excludePropertyNameList.contains(propertyName)) {

                continue;
            }

            String newPropertyName = StringUtils.EMPTY;
            if (MapUtils.isNotEmpty(customizePropertyNameMap)) {

                newPropertyName = customizePropertyNameMap.get(propertyName);
            }
            if (StringUtils.isBlank(newPropertyName)) {

                newPropertyName = propertyConvertFunc.apply(propertyName);
            }
            if (StringUtils.isBlank(newPropertyName)) {

                continue;
            }

            Class<?> originPropertyType = originPd.getPropertyType();
            for (PropertyDescriptor targetPd : targetPds) {

                if (newPropertyName.equals(targetPd.getName()) == false) {

                    continue;
                }

                Method originReadMethod = originPd.getReadMethod(),
                        targetWriteMethod = targetPd.getWriteMethod();
                if (originReadMethod != null && targetWriteMethod != null
                        && ClassUtils.isAssignable(targetWriteMethod.getParameterTypes()[0], originPropertyType)) {

                    try {
                        if (Modifier.isPublic(originReadMethod.getDeclaringClass().getModifiers()) == false) {

                            originReadMethod.setAccessible(true);
                        }
                        Object value = originReadMethod.invoke(originObj);
                        if (Modifier.isPublic(targetWriteMethod.getDeclaringClass().getModifiers()) == false) {

                            targetWriteMethod.setAccessible(true);
                        }
                        targetWriteMethod.invoke(targetObj, value);
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {

                        return null;
                    }
                }
            }
        }
        return targetObj;
    }

    /**
     * 驼峰转下划线
     *
     * @param camelStr
     * @return
     */
    public static String camel2Underline(String camelStr) {

        if (StringUtils.isEmpty(camelStr)) {

            return StringUtils.EMPTY;
        }

        int len = camelStr.length();
        StringBuilder strb = new StringBuilder(len + len >> 1);
        for (int i = 0; i < len; i++) {

            char c = camelStr.charAt(i);
            if (Character.isUpperCase(c)) {

                strb.append(UNDERLINE_CHAR);
                strb.append(Character.toLowerCase(c));
            } else {

                strb.append(c);
            }
        }
        return strb.toString();
    }

    /**
     * 下划线转驼峰
     *
     * @param underlineStr
     * @return
     */
    public static String underline2Camel(String underlineStr) {

        if (StringUtils.isEmpty(underlineStr)) {

            return StringUtils.EMPTY;
        }

        int len = underlineStr.length();
        StringBuilder strb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {

            char c = underlineStr.charAt(i);
            if (c == UNDERLINE_CHAR && (++i) < len) {

                c = underlineStr.charAt(i);
                strb.append(Character.toUpperCase(c));
            } else {

                strb.append(c);
            }
        }
        return strb.toString();
    }
}