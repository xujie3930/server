package com.szmsd.common.core.utils.bean;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 针对fasterxml.jackson使用的序列化工具类
 *
 * @author WangSenHao
 * @date 2020/3/30
 */
@Slf4j
public final class ObjectMapperUtils {

    private static ObjectMapper MAPPER;
    static {
        MAPPER = new ObjectMapper();
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    }

    /**
     * 构造新的destinationClass实例对象，通过source对象中的字段内容
     * 映射到destinationClass实例对象中，并返回新的destinationClass实例对象。
     *
     * @param source           源数据对象
     * @param destinationClass 要构造新的实例对象Class
     * @return T
     **/
    public static <T> T map(Object source, Class<T> destinationClass) {
        T t = null;
        try {
            t = ObjectUtils.defaultIfNull(MAPPER.readValue(MAPPER.writeValueAsString(source), destinationClass), destinationClass.newInstance());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return t;
    }

    /**
     * 构造新的destinationClass实例对象，通过source对象中的字段内容
     * 映射到destinationClass实例对象中，并返回新的destinationClass实例对象。
     *
     * @param sourceList       源数据对象集合
     * @param destinationClass 要构造新的实例对象Class
     * @return java.util.List<T>
     **/
    public static <T> List<T> mapList(Collection<?> sourceList, Class<T> destinationClass) {
        return sourceList.stream()
                .map(source -> map(source, destinationClass))
                .collect(Collectors.toList());
    }
}
