package com.szmsd.http.config;

import lombok.Data;
import org.springframework.http.HttpMethod;

import java.util.Map;

@Data
public class DomainTokenValue {

    // domain token 组件在SpringBean容器里的名称
    private String domainToken;
    // 授权码的url
    private String authorizeUrl;
    // 请求方式
    private HttpMethod authorizeHttpMethod;
    // 请求参数
    private Map<String, Object> authorizeRequestParams;
    // 请求头
    private Map<String, String> authorizeRequestHeaders;
    // token的url
    private String tokenUrl;
    // 请求方式
    private HttpMethod tokenHttpMethod;
    // 请求参数
    private Map<String, Object> tokenRequestParams;
    // 请求头
    private Map<String, String> tokenRequestHeaders;
    // 默认access token过期时间
    private long accessTokenExpiresIn;
    // 默认refresh token过期时间
    private long refreshTokenExpiresIn;
    // 默认access token
    private String defaultAccessToken;
    // 默认refresh token
    private String defaultRefreshToken;
}
