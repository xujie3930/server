package com.szmsd.auth.config;

import com.szmsd.common.core.constant.Constants;
import com.szmsd.common.core.domain.R;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

public class ThirdResourceOwnerPasswordTokenGranter extends ResourceOwnerPasswordTokenGranter {

    private ThirdAuthenticationConfig thirdAuthenticationConfig;
    private RestTemplate restTemplate;

    public ThirdResourceOwnerPasswordTokenGranter(AuthenticationManager authenticationManager, AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory) {
        super(authenticationManager, tokenServices, clientDetailsService, requestFactory);
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        // 验证自定义规则
        Map<String, String> requestParameters = tokenRequest.getRequestParameters();
        if (null != thirdAuthenticationConfig) {
            List<ThirdAuthenticationConfig.DataConfig> values = thirdAuthenticationConfig.getValues();
            if (CollectionUtils.isNotEmpty(values)) {
                for (ThirdAuthenticationConfig.DataConfig value : values) {
                    String matcher = value.getMatcher();
                    if (null == matcher) {
                        continue;
                    }
                    if (!matcher.equals(requestParameters.get("matcher"))) {
                        continue;
                    }
                    String url = "http://" + value.getApplicationName() + value.getUrl();
                    MultiValueMap<String, String> headers = new HttpHeaders();
                    headers.set("Content-Type", "application/json;charset=utf-8");
                    HttpEntity<?> httpEntity = new HttpEntity<>(requestParameters, headers);
                    try {
                        ResponseEntity<R<Object>> responseEntity = restTemplate.exchange(url, value.getHttpMethod(), httpEntity, new ParameterizedTypeReference<R<Object>>() {
                        });
                        HttpStatus statusCode = responseEntity.getStatusCode();
                        R<Object> responseEntityBody = responseEntity.getBody();
                        // 接口错误
                        if (!HttpStatus.OK.equals(statusCode) || null == responseEntityBody) {
                            throw new BadCredentialsException("认证失败，第三方认证接口返回异常");
                        }
                        if (Constants.SUCCESS != responseEntityBody.getCode()) {
                            // 直接抛出返回的错误信息
                            throw new BadCredentialsException(responseEntityBody.getMsg());
                        }
                    } catch (Exception e) {
                        throw new BadCredentialsException("认证失败，" + e.getMessage());
                    }
                    // 正常逻辑认证通过
                }
            }
        }
        return super.getOAuth2Authentication(client, tokenRequest);
    }

    public void setThirdAuthenticationConfig(ThirdAuthenticationConfig thirdAuthenticationConfig) {
        this.thirdAuthenticationConfig = thirdAuthenticationConfig;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
