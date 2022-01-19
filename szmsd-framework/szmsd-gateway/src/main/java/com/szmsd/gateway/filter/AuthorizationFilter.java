package com.szmsd.gateway.filter;

import com.szmsd.gateway.config.IgnoreConfig;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthorizationFilter implements GlobalFilter, Ordered {
    private final Logger logger = LoggerFactory.getLogger(AuthorizationFilter.class);
    private final PathMatcher pathMatcher = new AntPathMatcher();
    @Autowired
    private IgnoreConfig ignoreConfig;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        if (ignoreConfig.isEnabled()) {
            // 1. 获取Authorization
            String authorization = request.getHeaders().getFirst("Authorization");
            if (StringUtils.isEmpty(authorization)) {
                authorization = request.getHeaders().getFirst("authorization");
            }
            String ip = request.getHeaders().getFirst("Gateway-X-Access-IP");
            String path = request.getURI().getPath();
            boolean jump = false;
            if (CollectionUtils.isNotEmpty(ignoreConfig.getUrls())) {
                jump = ignoreConfig.getUrls().contains(path);
            }
            if (!jump && CollectionUtils.isNotEmpty(ignoreConfig.getMatchUrls())) {
                for (String url : ignoreConfig.getMatchUrls()) {
                    if (pathMatcher.match(url, path)) {
                        jump = true;
                        break;
                    }
                }
            }
            logger.info("当前请求的url:{}, method:{}, token:{}, ip:{}, jump:{}", path, request.getMethodValue(), authorization, ip, jump);
            if (!jump && StringUtils.isEmpty(authorization)) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
