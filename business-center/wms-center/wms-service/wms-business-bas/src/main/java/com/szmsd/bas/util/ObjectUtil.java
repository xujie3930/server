package com.szmsd.bas.util;

import org.apache.poi.ss.formula.functions.T;

import java.lang.reflect.Field;

public class ObjectUtil {
    public static void fillNull(Object obj, Object obj2) throws IllegalAccessException {
        for (Field f : obj.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            for (Field fi : obj2.getClass().getDeclaredFields()) {
                fi.setAccessible(true);
                //判断字段是否为空，并且对象属性中的基本都会转为对象类型来判断
                if (f.get(obj) == null&&fi.getName().equals(f.getName())) {
                    f.set(obj,fi.get(obj2));
                }
            }
        }
    }
}
