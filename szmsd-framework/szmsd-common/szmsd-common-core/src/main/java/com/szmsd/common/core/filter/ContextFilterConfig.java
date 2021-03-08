package com.szmsd.common.core.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangyuyuan
 * @date 2020-11-20 020 16:43
 */
@Configuration
public class ContextFilterConfig {

    @Bean
    public FilterRegistrationBean<ContextFilter> getRequestFilter() {
        final FilterRegistrationBean<ContextFilter> filter = new FilterRegistrationBean<>();
        filter.setFilter(new ContextFilter());
        filter.setName("ContextFilter");
        filter.addUrlPatterns("/*");
        filter.setOrder(Integer.MIN_VALUE);
        return filter;
    }
}
