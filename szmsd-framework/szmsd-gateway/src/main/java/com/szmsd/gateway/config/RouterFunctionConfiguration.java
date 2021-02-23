package com.szmsd.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import com.szmsd.gateway.handler.HystrixFallbackHandler;
import com.szmsd.gateway.handler.ValidateCodeHandler;

import javax.annotation.Resource;

/**
 * 路由配置信息
 * 
 * @author szmsd
 */
@Configuration
public class RouterFunctionConfiguration
{
    @Resource
    private HystrixFallbackHandler hystrixFallbackHandler;

    @Resource
    private ValidateCodeHandler imageCodeHandler;

    @SuppressWarnings("rawtypes")
    @Bean
    public RouterFunction routerFunction()
    {
        return RouterFunctions
                .route(RequestPredicates.path("/fallback").and(RequestPredicates.accept(MediaType.TEXT_PLAIN)),
                        hystrixFallbackHandler)
                .andRoute(RequestPredicates.GET("/code").and(RequestPredicates.accept(MediaType.TEXT_PLAIN)),
                        imageCodeHandler);
    }

}
