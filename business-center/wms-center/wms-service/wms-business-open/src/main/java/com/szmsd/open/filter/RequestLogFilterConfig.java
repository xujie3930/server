package com.szmsd.open.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangyuyuan
 * @date 2021-03-06 17:13
 */
@Configuration
public class RequestLogFilterConfig {

    @Bean
    public FilterRegistrationBean<RequestLogFilter> getTransactionFilter() {
        final FilterRegistrationBean<RequestLogFilter> filter = new FilterRegistrationBean<>();
        filter.setFilter(new RequestLogFilter());
        filter.setName("RequestLogFilter");
        filter.addUrlPatterns("/api/*");
        filter.setOrder(-100);
        return filter;
    }
}
