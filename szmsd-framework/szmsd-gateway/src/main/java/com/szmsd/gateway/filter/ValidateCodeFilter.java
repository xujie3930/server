package com.szmsd.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.szmsd.common.core.constant.HttpStatus;
import com.szmsd.common.core.constant.SecurityConstants;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.gateway.service.ValidateCodeService;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * 验证码过滤器
 *
 * @author szmsd
 */
@Component
public class ValidateCodeFilter extends AbstractGatewayFilterFactory<Object> {
    private final static String AUTH_URL = "/oauth/token";

    @Resource
    private ValidateCodeService validateCodeService;

    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) -> {
            R r = new R<>();

              ServerHttpRequest request = exchange.getRequest();

            // 非登录请求，不处理
            if (!StringUtils.containsIgnoreCase(request.getURI().getPath(), AUTH_URL)) {
                return chain.filter(exchange);
            }
            // 授权码不验证
            if (request.getQueryParams().containsKey("grant_type") && "authorization_code".equals(request.getQueryParams().getFirst("grant_type"))) {
                return chain.filter(exchange);
            }
            //如果是app请求，不处理
            if(request.getQueryParams().containsKey("client_id")&&request.getQueryParams().getFirst(SecurityConstants.DETAILS_CLIENT_ID).equals(SecurityConstants.DETAILS_CLIENT_ID_APP)&&
                    !request.getQueryParams().containsKey("code") && !request.getQueryParams().containsKey("uuid")){
                return chain.filter(exchange);
            }
            // 消息头存在内容，且不存在验证码参数，不处理
            String header = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (StringUtils.isNotEmpty(header) && StringUtils.startsWith(header, "Basic")
                    && !request.getQueryParams().containsKey("code") && !request.getQueryParams().containsKey("uuid")) {
                return chain.filter(exchange);
            }
            try {
                validateCodeService.checkCapcha(request.getQueryParams().getFirst("code"),
                        request.getQueryParams().getFirst("uuid"));
            } catch (Exception e) {
                r.setCode(HttpStatus.ERROR);
                r.setMsg(e.getMessage());
                r.setData("");
                ServerHttpResponse response = exchange.getResponse();
                return exchange.getResponse().writeWith(
                        Mono.just(response.bufferFactory().wrap(JSON.toJSONBytes(r))));
            }
            return chain.filter(exchange);
        };
    }
}
