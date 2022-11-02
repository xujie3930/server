package com.szmsd.auth.controller;

import com.alibaba.fastjson.JSON;
import com.szmsd.auth.util.AuthResult;
import com.szmsd.auth.util.LoginResponse;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.IdUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/oauth")
public class OauthController {
    private static final Logger logger = LoggerFactory.getLogger(IdUtils.class);


    @Autowired
    private TokenEndpoint tokenEndpoint;

    private Set<HttpMethod> allowedRequestMethods = new HashSet<HttpMethod>(Arrays.asList(HttpMethod.GET));

    /**
     *  重写/oauth/token默认接口，返回的数据格式统一
     * @param principal
     * @param parameters
     * @return
     * @throws HttpRequestMethodNotSupportedException
     */
    @RequestMapping(value = "/token", method=RequestMethod.GET)
    public R<AuthResult> getAccessToken(Principal principal, @RequestParam
    Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        if (!allowedRequestMethods.contains(HttpMethod.GET)) {
            throw new HttpRequestMethodNotSupportedException("GET");
        }
        return newpostAccessToken(principal, parameters);
    }

    /**
     * 重写/oauth/token默认接口，返回的数据格式统一
     */
    @PostMapping(value = "/token")
    public R<AuthResult> newpostAccessToken(Principal principal, @RequestParam
    Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        OAuth2AccessToken accessToken = tokenEndpoint.postAccessToken(principal, parameters).getBody();

        String matcher = parameters.get("matcher");

        AuthResult authResult = new AuthResult();
        authResult.setMatcher(matcher);
        authResult.setAccessToken(accessToken);

        return R.ok(authResult);
    }

    private ResponseEntity<AuthResult> getResponse(AuthResult accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cache-Control", "no-store");
        headers.set("Pragma", "no-cache");
        headers.set("Content-Type", "application/json;charset=UTF-8");
        return new ResponseEntity<AuthResult>(accessToken, headers, HttpStatus.OK);
    }

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
        paramsMap.set("matcher",map.get("matcher"));
        paramsMap.set("shopifyUUID",map.get("shopifyUUID"));

        logger.info("loginApp params {}", JSON.toJSONString(paramsMap));

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
        loginMap.put("matcher",map.get("matcher"));
        loginMap.put("shopifyUUID",map.get("shopifyUUID"));

        logger.info("LoginResponse loginMap {}", JSON.toJSONString(loginMap));

        //返回的封装
        return new LoginResponse(200, loginMap);
    }

}
