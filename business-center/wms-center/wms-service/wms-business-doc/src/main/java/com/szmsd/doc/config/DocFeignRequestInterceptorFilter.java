package com.szmsd.doc.config;

import com.szmsd.common.core.feign.FeignRequestInterceptorFilter;
import org.springframework.stereotype.Component;

import java.util.Enumeration;

/**
 * @author zhangyuyuan
 * @date 2021-08-02 9:55
 */
@Component
public class DocFeignRequestInterceptorFilter implements FeignRequestInterceptorFilter {

    @Override
    public boolean filter(String headerName, Enumeration<String> headerValues) {
        return "authorization".equalsIgnoreCase(headerName);
    }
}
