package com.szmsd.doc.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangyuyuan
 * @date 2021-07-31 15:02
 */
@Slf4j
@Configuration
public class DocRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        // 处理认证传播
        requestTemplate.removeHeader("Authorization");
    }
}
