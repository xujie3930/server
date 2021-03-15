package com.szmsd.bas.util;

import org.apache.poi.ss.formula.functions.T;

import java.lang.reflect.Field;

public class ObjectUtil {
    public static void findNull(Object obj, T t) throws IllegalAccessException {
        for (Field f : obj.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            if (f.get(obj) == null) {
                //判断字段是否为空，并且对象属性中的基本都会转为对象类型来判断
                f.getName();
                //f.set();

            }
        }
    }
}
