package com.szmsd.common.core.feign;

import java.util.Enumeration;

/**
 * @author zhangyuyuan
 * @date 2021-08-02 9:49
 */
public interface FeignRequestInterceptorFilter {

    /**
     * header过滤
     * <p>
     * 返回true表示过滤这个header信息
     * <p>
     * 返回false表示不过滤header信息，默认false
     *
     * @param headerName   header名称
     * @param headerValues header值
     * @return 是否过滤
     */
    default boolean filter(String headerName, Enumeration<String> headerValues) {
        return false;
    }

    public class DefaultFeignRequestInterceptorFilter implements FeignRequestInterceptorFilter {

    }
}
