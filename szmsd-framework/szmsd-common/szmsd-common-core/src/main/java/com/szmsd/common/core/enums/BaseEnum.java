package com.szmsd.common.core.enums;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @FileName BaseEnum.java
 * @Description 枚举值类型
 * @Date 2020-06-15 14:18
 * @Author Yan Hua
 * @Version 1.0
 */
@SuppressWarnings("unused")
public interface BaseEnum {

    String getKey();

    String getValue();

    static <E extends BaseEnum> boolean isKeyExists(Class<E> enumClass, String key) {
        if (StringUtils.isEmpty(key)) {
            return false;
        }
        List<String> keys = getKeyList(enumClass);
        if (CollectionUtils.isEmpty(keys)) {
            return false;
        }
        return keys.contains(key);
    }

    static <E extends BaseEnum> boolean isValueExists(Class<E> enumClass, String value) {
        if (StringUtils.isEmpty(value)) {
            return false;
        }
        List<String> values = getValueList(enumClass);
        if (CollectionUtils.isEmpty(values)) {
            return false;
        }
        return values.contains(value);
    }

    static <E extends BaseEnum> List<String> getKeyList(Class<E> enumClass) {
        List<String> list = new ArrayList<>();
        if (!BaseEnum.class.isAssignableFrom(enumClass)) {
            return list;
        }
        for (E e : enumClass.getEnumConstants()) {
            list.add(e.getKey());
        }
        return list;
    }

    static <E extends BaseEnum> List<String> getValueList(Class<E> enumClass) {
        List<String> list = new ArrayList<>();
        if (!BaseEnum.class.isAssignableFrom(enumClass)) {
            return list;
        }
        for (E e : enumClass.getEnumConstants()) {
            list.add(e.getValue());
        }
        return list;
    }

    static <E extends BaseEnum> List<Map<String, String>> getMapList(Class<E> enumClass) {
        List<Map<String, String>> mapList = new ArrayList<>();
        if (!BaseEnum.class.isAssignableFrom(enumClass)) {
            return mapList;
        }
        for (E e : enumClass.getEnumConstants()) {
            Map<String, String> map = new HashMap<>();
            map.put("key", e.getKey());
            map.put("value", e.getValue());
            mapList.add(map);
        }
        return mapList;
    }

    static List<Map<String, String>> getMapList(BaseEnum... enumData) {
        List<Map<String, String>> mapList = new ArrayList<>();
        for (BaseEnum ed : enumData) {
            Map<String, String> map = new HashMap<>();
            map.put("key", ed.getKey());
            map.put("value", ed.getValue());
            mapList.add(map);
        }
        return mapList;
    }

    static Map<String, String> getMap(BaseEnum enumData) {
        Map<String, String> enumMap = new HashMap<>();
        enumMap.put("key", enumData.getKey());
        enumMap.put("value", enumData.getValue());
        return enumMap;
    }

    static <E extends BaseEnum> String key2Value(Class<E> enumClass, String key) {
        if (!BaseEnum.class.isAssignableFrom(enumClass)) {
            return "";
        }
        for (E e : enumClass.getEnumConstants()) {
            if (e.getKey().equals(key)) {
                return e.getValue();
            }
        }
        return "";
    }

    static <E extends BaseEnum> List<String> key2Value(Class<E> enumClass, List<String> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return new ArrayList<>();
        }
        List<String> values = new ArrayList<>();
        for (String key : keys) {
            values.add(key2Value(enumClass, key));
        }
        return values;
    }

    static <E extends BaseEnum> E key2Enum(Class<E> enumClass, String key) {
        if (!BaseEnum.class.isAssignableFrom(enumClass)) {
            return null;
        }
        for (E e : enumClass.getEnumConstants()) {
            if (e.getKey().equals(key)) {
                return e;
            }
        }
        return null;
    }

    static <E extends BaseEnum> List<E> key2Enum(Class<E> enumClass, List<String> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return new ArrayList<>();
        }
        List<E> enums = new ArrayList<>();
        for (String key : keys) {
            enums.add(key2Enum(enumClass, key));
        }
        return enums;
    }

    static <E extends BaseEnum> String value2Key(Class<E> enumClass, String value) {
        if (!BaseEnum.class.isAssignableFrom(enumClass)) {
            return "";
        }
        for (E e : enumClass.getEnumConstants()) {
            if (e.getValue().equalsIgnoreCase(value)) {
                return e.getKey();
            }
        }
        return "";
    }

    static <E extends BaseEnum> List<String> value2Key(Class<E> enumClass, List<String> values) {
        if (CollectionUtils.isEmpty(values)) {
            return new ArrayList<>();
        }
        List<String> keys = new ArrayList<>();
        for (String value : values) {
            keys.add(value2Key(enumClass, value));
        }
        return keys;
    }

    static <E extends BaseEnum> E value2Enum(Class<E> enumClass, String value) {
        if (!BaseEnum.class.isAssignableFrom(enumClass)) {
            return null;
        }
        for (E e : enumClass.getEnumConstants()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }
        return null;
    }

    static <E extends BaseEnum> List<E> value2Enum(Class<E> enumClass, List<String> values) {
        if (CollectionUtils.isEmpty(values)) {
            return new ArrayList<>();
        }
        List<E> enums = new ArrayList<>();
        for (String value : values) {
            enums.add(value2Enum(enumClass, value));
        }
        return enums;
    }

}
