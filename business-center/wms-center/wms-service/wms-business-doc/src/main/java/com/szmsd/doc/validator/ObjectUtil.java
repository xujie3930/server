package com.szmsd.doc.validator;

import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

/**
 * @author zhangyuyuan
 * @date 2021-08-03 17:46
 */
public class ObjectUtil {

    /**
     * 转成get方法
     *
     * @param str str，参考格式userName
     * @return getUserName
     */
    public static String convertToGetMethod(String str) {
        return "get" +
                str.substring(0, 1).toUpperCase() +
                str.substring(1);
    }

    /**
     * 获取值
     *
     * @param object    对象
     * @param getMethod get方法
     * @return 返回值
     */
    public static Object getValue(Object object, String getMethod) {
        try {
            return MethodUtils.invokeMethod(object, getMethod);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }

    /**
     * 判断字段是否为空
     *
     * @param o      对象
     * @param fields 字段，字段要符合get模式，不能有is开头的
     * @return boolean，如果fields所有的字段都是空返回false，其中任何一个字段有值返回true
     */
    public static boolean isAnyNull(Object o, String[] fields) {
        boolean anyNull = false;
        for (String field : fields) {
            if (Objects.nonNull(getValue(o, convertToGetMethod(field)))) {
                anyNull = true;
                break;
            }
        }
        return anyNull;
    }

    /**
     * 验证所有的字段都不能为空
     *
     * @param o      o
     * @param fields fields
     * @return boolean，所有字段都不是空时返回true，其中一个字段为空返回false
     */
    public static boolean isAllAnyNull(Object o, String[] fields) {
        boolean anyNull = true;
        for (String field : fields) {
            if (Objects.isNull(getValue(o, convertToGetMethod(field)))) {
                anyNull = false;
                break;
            }
        }
        return anyNull;
    }
}
