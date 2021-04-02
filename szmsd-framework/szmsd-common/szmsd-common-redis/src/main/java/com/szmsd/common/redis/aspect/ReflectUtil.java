package com.szmsd.common.redis.aspect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ReflectUtil {

    private static final Logger logger = LoggerFactory.getLogger(ReflectUtil.class);

    /**
     * 通过fieldName获取值
     *
     * @param instance  实例
     * @param fieldName 字段
     * @return 字段值
     */
    public static Object getValue(Object instance, String fieldName) {
        try {
            Field field = getField(instance.getClass(), fieldName);
            // 参数值为true，禁用访问控制检查
            if (Objects.nonNull(field)) {
                field.setAccessible(true);
                return field.get(instance);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    public static Field getField(Class<?> thisClass, String fieldName) throws NoSuchFieldException {
        if (fieldName == null) {
            throw new NoSuchFieldException("Error field !");
        }
        List<Field> fieldList = getFieldList(thisClass);
        // 返回相应字段属性值
        for (Field item : fieldList) {
            if (item.getName().equals(fieldName)) {
                // 子类存在则直接返回
                return item;
            }
        }
        return null;
    }

    public static List<Field> getFieldList(Class<?> thisClass) {
        // 绑定当前类、及父类所有属性合并
        // 获取当前实体类的所有属性（不包含继承的类的属性），返回Field数组
        Field[] fieldChi = thisClass.getDeclaredFields();
        List<Field> fieldList = new ArrayList<>(Arrays.asList(fieldChi));
        // 获取该实体类父类所有属性
        Field[] fieldParent = thisClass.getSuperclass().getDeclaredFields();
        fieldList.addAll(Arrays.asList(fieldParent));
        return fieldList;
    }
}
