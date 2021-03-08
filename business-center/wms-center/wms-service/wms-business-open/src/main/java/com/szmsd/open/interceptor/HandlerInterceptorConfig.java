package com.szmsd.open.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author zhangyuyuan
 * @date 2021-03-06 16:50
 */
@Configuration
public class HandlerInterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private AuthHandlerInterceptor authHandlerInterceptor;
    @Autowired
    private TransactionHandlerInterceptor transactionHandlerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 验证
        registry.addInterceptor(authHandlerInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/*.html", "/webjars/**", "/swagger-resources/**");
        // 事务验证
        registry.addInterceptor(transactionHandlerInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/*.html", "/webjars/**", "/swagger-resources/**");
    }
}
