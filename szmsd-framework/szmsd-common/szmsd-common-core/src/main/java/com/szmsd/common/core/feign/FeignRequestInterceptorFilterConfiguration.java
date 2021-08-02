package com.szmsd.common.core.feign;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangyuyuan
 * @date 2021-08-02 9:50
 */
@Configuration
public class FeignRequestInterceptorFilterConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public FeignRequestInterceptorFilter defaultFeignRequestInterceptorFilter() {
        return new FeignRequestInterceptorFilter.DefaultFeignRequestInterceptorFilter();
    }
}
