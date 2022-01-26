package com.szmsd.http.plugins;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.filter.UUIDUtil;
import com.szmsd.common.core.utils.HttpClientHelper;
import com.szmsd.common.core.utils.HttpResponseBody;
import com.szmsd.http.config.DomainTokenValue;
import com.szmsd.http.utils.RedirectUriUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component("TrackingYeeToken")
public class TrackingYeeToken extends AbstractDomainToken {

    @Override
    TokenValue internalGetTokenValue() {
        DomainTokenValue domainTokenValue = super.getDomainTokenValue();
        // 处理默认token
        String defaultAccessToken = domainTokenValue.getDefaultAccessToken();
        String defaultRefreshToken = domainTokenValue.getDefaultRefreshToken();
        if (StringUtils.isNotEmpty(defaultAccessToken) && StringUtils.isNotEmpty(defaultRefreshToken)) {
            // 直接返回
            TokenValue tokenValue = new TokenValue();
            // 前缀
            tokenValue.setAccessToken("Bearer " + defaultAccessToken);
            tokenValue.setExpiresIn(domainTokenValue.getAccessTokenExpiresIn());
            tokenValue.setRefreshToken(defaultRefreshToken);
            return tokenValue;
        }
        String authorizeUrl = domainTokenValue.getAuthorizeUrl();
        HttpMethod authorizeHttpMethod = domainTokenValue.getAuthorizeHttpMethod();
        String stateKey = UUIDUtil.uuid();
        Map<String, Object> authorizeRequestParams = domainTokenValue.getAuthorizeRequestParams();
        if (null == authorizeRequestParams) {
            authorizeRequestParams = new HashMap<>();
        }
        authorizeRequestParams.put("state", stateKey);
        String authorizeRequestBody = JSON.toJSONString(authorizeRequestParams);
        HttpResponseBody authorizeHttpResponseBody;
        // https://auth.trackingyee.com/connect/authorize?response_type=code&client_id=YTdjNmJiZTQtMTU5MC00MjViLTg5MzEtZmNkNmQ1ZDlkNGJi&redirect_uri=http://183.3.221.136:22221/api/wms-http/api/redirect/uri&scope=basic&state=ok
        if (HttpMethod.GET.equals(authorizeHttpMethod)) {
            authorizeHttpResponseBody = HttpClientHelper.httpGet(authorizeUrl, authorizeRequestBody, domainTokenValue.getAuthorizeRequestHeaders());
        } else if (HttpMethod.POST.equals(authorizeHttpMethod)) {
            authorizeHttpResponseBody = HttpClientHelper.httpPost(authorizeUrl, authorizeRequestBody, domainTokenValue.getAuthorizeRequestHeaders());
        } else {
            throw new CommonException("999", "不支持的请求方式，" + authorizeHttpMethod);
        }
        super.logger.info("请求获取授权码响应结果：{}", JSON.toJSONString(authorizeHttpResponseBody));
        int authorizeStatus = authorizeHttpResponseBody.getStatus();
        if (HttpStatus.OK.value() != authorizeStatus) {
            String body = authorizeHttpResponseBody.getBody();
            throw new CommonException("999", "获取TrackingYee授权码失败，错误信息：" + body);
        }
        // 等待获取授权码
        String wrapRedirectUriKey = RedirectUriUtil.wrapRedirectUriKey(stateKey);
        // 记录开始的时间，单位毫秒
        long currentTimeMillis = System.currentTimeMillis();
        boolean delayState = true;
        String authorizationCode = null;
        do {
            Object authorizationObject = super.redisTemplate.opsForValue().get(wrapRedirectUriKey);
            if (null != authorizationObject) {
                // 获取到授权码
                authorizationCode = String.valueOf(authorizationObject);
                delayState = false;
                // 删除缓存
                this.redisTemplate.delete(wrapRedirectUriKey);
            }
            // 等待1分钟
            if (delayState && (System.currentTimeMillis() - currentTimeMillis > 60000)) {
                // 超时中断
                delayState = false;
            }
            try {
                // 休眠1秒
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                super.logger.error(e.getMessage(), e);
            }
        } while (delayState);
        if (null == authorizationCode) {
            // 最后尝试获取授权码
            Object authorizationObject = super.redisTemplate.opsForValue().get(wrapRedirectUriKey);
            if (null != authorizationObject) {
                authorizationCode = String.valueOf(authorizationObject);
            } else {
                throw new CommonException("999", "获取TrackingYee授权码失败，未获取到授权码，请求处理超时1分钟");
            }
        }
        // 根据授权码获取token信息
        return this.tokenRequest("authorization_code", authorizationCode, null);
    }

    @Override
    TokenValue internalGetTokenValueByRefreshToken(String refreshToken) {
        // 根据refresh token获取token信息
        return this.tokenRequest("refresh_token", null, refreshToken);
    }

    private TokenValue tokenRequest(String grantType, String authorizationCode, String refreshToken) {
        String tokenUrl = domainTokenValue.getTokenUrl();
        HttpMethod tokenHttpMethod = domainTokenValue.getTokenHttpMethod();
        Map<String, Object> tokenRequestParams = domainTokenValue.getTokenRequestParams();
        if (null == tokenRequestParams) {
            tokenRequestParams = new HashMap<>();
        }

        // 授权码
        // grant_type=authorization_code
        // client_id=3wSk8Ta3Eo7ReX8SVyrN
        // client_secret=9e4b5215fdb02g03b9q357c4dw1b6cce
        // redirect_uri=http://www.example.com/oauth_redirect
        // code=EXNFesbfEEOXsogJOtQObJQdvkRpl

        // refresh token
        // grant_type=refresh_token
        // client_id=3wSk8Ta3Eo7ReX8SVyrN
        // client_secret=9e4b5215fdb02g03b9q357c4dw1b6cce
        // refresh_token=4pVLNU5XHEQ2Z5E3AHY
        // scope=basic

        tokenRequestParams.put("grant_type", grantType);
        if (null != authorizationCode) {
            tokenRequestParams.put("code", authorizationCode);
        }
        if (null != refreshToken) {
            tokenRequestParams.put("refresh_token", refreshToken);
            tokenRequestParams.put("scope", "basic");
        }
        HttpResponseBody tokenHttpResponseBody;
        if (HttpMethod.GET.equals(tokenHttpMethod)) {
            String tokenRequestBody = JSON.toJSONString(tokenRequestParams);
            tokenHttpResponseBody = HttpClientHelper.httpGet(tokenUrl, tokenRequestBody, domainTokenValue.getTokenRequestHeaders());
        } else if (HttpMethod.POST.equals(tokenHttpMethod)) {
            tokenHttpResponseBody = HttpClientHelper.httpPost(tokenUrl, tokenRequestParams, domainTokenValue.getTokenRequestHeaders());
        } else {
            throw new CommonException("999", "不支持的请求方式，" + tokenHttpMethod);
        }
        super.logger.info("请求获取Token响应结果：{}", JSON.toJSONString(tokenHttpResponseBody));
        int tokenStatus = tokenHttpResponseBody.getStatus();
        String body = tokenHttpResponseBody.getBody();
        if (HttpStatus.OK.value() != tokenStatus) {
            throw new CommonException("999", "获取TrackingYee Token失败，错误信息：" + body);
        }
        JSONObject jsonObject = JSON.parseObject(body);
        if (null != jsonObject) {
            TokenValue tokenValue = new TokenValue();
            // 前缀
            tokenValue.setAccessToken("Bearer " + jsonObject.getString("access_token"));
            tokenValue.setExpiresIn(jsonObject.getLongValue("expires_in"));
            tokenValue.setScope(jsonObject.getString("scope"));
            tokenValue.setRefreshToken(jsonObject.getString("refresh_token"));
            return tokenValue;
        }
        return null;
    }

}
