package com.szmsd.common.core.utils.bean;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.poi.ss.formula.functions.T;
import org.dozer.DozerBeanMapper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;
@Slf4j
public final class BeanMapperUtil {

    private static DozerBeanMapper mapper = new DozerBeanMapper();

    private BeanMapperUtil() {
    }

    public static DozerBeanMapper getMapper() {
        return mapper;
    }

    /**
     * 构造新的destinationClass实例对象，通过source对象中的字段内容
     * 映射到destinationClass实例对象中，并返回新的destinationClass实例对象。
     *
     * @param source           源数据对象
     * @param destinationClass 要构造新的实例对象Class
     */
    public static <T> T map(Object source, Class<T> destinationClass) {
        return getMapper().map(source, destinationClass);
    }

    public static <T> List<T> mapList(Collection<?> sourceList, Class<T> destinationClass) {
        return Optional.ofNullable(sourceList).orElse(Collections.emptyList()).stream()
                .map(source -> getMapper().map(source, destinationClass))
                .collect(Collectors.toList());
    }


    /**
     * 将对象source的所有属性值拷贝到对象destination中.
     *
     * @param source            对象source
     * @param destinationObject destinationObject
     */
    public static void map(Object source, Object destinationObject) {
        getMapper().map(source, destinationObject);
    }

    public static Map<String, Object> objectToMap(Object obj) {
        try {
            if (obj == null) {
                return null;
            }
            Map<String, Object> map = new HashMap<String, Object>();
            Field[] declaredFields = obj.getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                field.setAccessible(true);
                if(List.class.isAssignableFrom(field.getType())) {
                    List<Map<String,Object>> r = new ArrayList<>();
                    Type t = field.getGenericType();
                    if (t instanceof ParameterizedType) {
                        if (!field.isAccessible()) {
                            field.setAccessible(true);
                        }
                        //获取到属性值的字节码
                        try {
                            Class<?> clzz = field.get(obj).getClass();
                            //反射调用获取到list的size方法来获取到集合的大小
                            Method sizeMethod = clzz.getDeclaredMethod("size");
                            if (!sizeMethod.isAccessible()) {
                                sizeMethod.setAccessible(true);
                            }
                            //集合长度
                            int size = (int) sizeMethod.invoke(field.get(obj));
                            //循环遍历获取到数据
                            for (int i = 0; i < size; i++) {
                                //反射获取到list的get方法
                                Method getMethod = clzz.getDeclaredMethod("get", int.class);
                                //调用get方法获取数据
                                if (!getMethod.isAccessible()) {
                                    getMethod.setAccessible(true);
                                }
                                Object invoke = getMethod.invoke(field.get(obj), i);
                                Map<String, Object> stringObjectMap = objectToMap(invoke);
                                r.add(stringObjectMap);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        map.put(field.getName(), r);
                    }
                }else{
                    map.put(field.getName(), field.get(obj));
                }
            }
            return map;

        } catch (Exception e) {
            log.error("MapObjectUtils:objectToMap", e);
        }
        return null;
    }
}
