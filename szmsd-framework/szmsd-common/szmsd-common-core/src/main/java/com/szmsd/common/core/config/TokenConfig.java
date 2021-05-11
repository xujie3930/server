package com.szmsd.common.core.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Set;

/**
 * @author zhangyuyuan
 * @date 2021-05-10 11:29
 */
@Data
@Component
@ConfigurationProperties(prefix = TokenConfig.CONFIG_PREFIX)
public class TokenConfig {

    static final String CONFIG_PREFIX = "com.szmsd.toekn";

    /**
     * 是否启用，默认启用
     */
    private boolean enabled = true;

    /**
     * redis有效期，单位秒，默认5秒，不写单位默认毫秒
     */
    private Duration time = Duration.of(5, ChronoUnit.SECONDS);

    /**
     * 拦截的请求，默认/**
     */
    private Set<String> includes;

    /**
     * 排除的请求
     * excludes 高于 includes
     */
    private Set<String> excludes;

    /**
     * 项目名称，默认<code>${spring.application.name}</code>
     */
    @Value("${com.szmsd.toekn.name:${spring.application.name:unknown}}")
    private String name;

    /**
     * 获取token，默认/get/token
     */
    private String getTokenUrl;
}
