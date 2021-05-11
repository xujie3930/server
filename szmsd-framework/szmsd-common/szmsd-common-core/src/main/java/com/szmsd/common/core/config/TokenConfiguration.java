package com.szmsd.common.core.config;

import com.szmsd.common.core.interceptor.GetTokenHandlerInterceptor;
import com.szmsd.common.core.interceptor.ResetTokenHandlerExceptionResolver;
import com.szmsd.common.core.interceptor.TokenConstant;
import com.szmsd.common.core.interceptor.TokenHandlerInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.*;

/**
 * @author zhangyuyuan
 * @date 2021-05-10 11:26
 */
@ConditionalOnClass(WebMvcConfigurer.class)
@ConditionalOnExpression("${com.szmsd.toekn.enabled:true}")
@ComponentScan(basePackages = {"com.szmsd.common.core.config", "com.szmsd.common.core.interceptor"})
@Configuration
public class TokenConfiguration implements WebMvcConfigurer {

    private final RedisTemplate<Object, Object> redisTemplate;
    private final TokenConfig tokenConfig;

    public TokenConfiguration(RedisTemplate<Object, Object> redisTemplate, TokenConfig tokenConfig) {
        this.redisTemplate = redisTemplate;
        this.tokenConfig = tokenConfig;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // get token
        registry.addInterceptor(new GetTokenHandlerInterceptor())
                .addPathPatterns(this.handlerGetTokenPath());
        // token
        registry.addInterceptor(new TokenHandlerInterceptor(this.redisTemplate, this.tokenConfig))
                .addPathPatterns(this.handlerIncludes())
                .excludePathPatterns(this.handlerExcludes());
    }

    @Override
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        resolvers.add(0, new ResetTokenHandlerExceptionResolver(this.redisTemplate));
    }

    private String handlerGetTokenPath() {
        String url = this.tokenConfig.getGetTokenUrl();
        if (StringUtils.isEmpty(url)) {
            return TokenConstant.GET_TOKEN_URL;
        }
        return url;
    }

    private List<String> handlerIncludes() {
        Set<String> includes = this.tokenConfig.getIncludes();
        if (Objects.isNull(includes)) {
            includes = new HashSet<>();
            includes.add("/**");
        }
        return new ArrayList<>(includes);
    }

    private List<String> handlerExcludes() {
        Set<String> excludes = this.tokenConfig.getExcludes();
        if (Objects.isNull(excludes)) {
            excludes = new HashSet<>();
        }
        return new ArrayList<>(excludes);
    }
}
