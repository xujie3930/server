package com.szmsd.auth.controller;

import com.szmsd.auth.util.LoginResponse;
import com.szmsd.common.core.utils.IdUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/oauth")
public class OauthController {
    private static final Logger logger = LoggerFactory.getLogger(IdUtils.class);

    @GetMapping("/loginApp")
    public LoginResponse login(@RequestParam Map<String, Object> map) {
        MultiValueMap<String, Object> paramsMap = new LinkedMultiValueMap<>();
        //请求的参数
        paramsMap.set("username", map.get("username"));
        paramsMap.set("password", map.get("password"));
        paramsMap.set("code", map.get("code"));
        paramsMap.set("uuid", map.get("uuid"));
        paramsMap.set("client_id", map.get("client_id"));
        paramsMap.set("client_secret", map.get("client_secret"));
        paramsMap.set("grant_type", map.get("grant_type"));
        paramsMap.set("scope", map.get("scope"));
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(map.get("client_id").toString(), map.get("client_secret").toString()));
        OAuth2AccessToken token = restTemplate.postForObject("http://localhost/dev-api/auth/oauth/token", paramsMap, OAuth2AccessToken.class);
        //返回的Date
        Map loginMap = new HashMap();
        loginMap.put("access_token", token.getValue());
        loginMap.put("expires_in", token.getExpiresIn());
        loginMap.put("refresh_token", token.getRefreshToken().getValue());
        Set scopeSet = token.getScope();
        for (Object o : scopeSet) {
            loginMap.put("scope", o);
        }
        loginMap.put("token_type", token.getTokenType());
        loginMap.put("user_id", token.getAdditionalInformation().get("user_id"));
        loginMap.put("username", token.getAdditionalInformation().get("username"));
        //返回的封装
        return new LoginResponse(200, loginMap);
    }

}
