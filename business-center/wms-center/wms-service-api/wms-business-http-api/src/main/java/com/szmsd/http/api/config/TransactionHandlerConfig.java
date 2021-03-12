package com.szmsd.http.api.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangyuyuan
 * @date 2021-03-10 10:13
 */
@Configuration
@ComponentScan(basePackages = {"com.szmsd.http.api.controller", "com.szmsd.http.api.feign", "com.szmsd.http.api.service"})
public class TransactionHandlerConfig {
}
