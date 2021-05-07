package com.szmsd.http.config;

import com.szmsd.http.servlet.matcher.PathContext;
import com.szmsd.http.servlet.matcher.PathRequestForwardMatcher;
import com.szmsd.http.servlet.matcher.RequestForwardMatcher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangyuyuan
 * @date 2021-05-07 10:21
 */
@Configuration
public class RequestForwardMatcherAutoConfig {

    private final PathContext pathContext;

    public RequestForwardMatcherAutoConfig(PathContext pathContext) {
        this.pathContext = pathContext;
    }

    @ConditionalOnMissingBean(RequestForwardMatcher.class)
    @Bean
    public RequestForwardMatcher defaultRequestForwardMatcher() {
        return new PathRequestForwardMatcher(pathContext);
    }
}
