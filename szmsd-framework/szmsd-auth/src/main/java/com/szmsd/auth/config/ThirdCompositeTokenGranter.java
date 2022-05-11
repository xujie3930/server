package com.szmsd.auth.config;

import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.TokenRequest;

import java.util.ArrayList;
import java.util.List;

public class ThirdCompositeTokenGranter implements TokenGranter {

    private final List<TokenGranter> tokenGranters;

    public ThirdCompositeTokenGranter(List<TokenGranter> tokenGranters) {
        this.tokenGranters = new ArrayList<>(tokenGranters);
    }

    @Override
    public OAuth2AccessToken grant(String grantType, TokenRequest tokenRequest) {
        for (TokenGranter granter : tokenGranters) {
            OAuth2AccessToken grant = granter.grant(grantType, tokenRequest);
            if (grant != null) {
                return grant;
            }
        }
        return null;
    }

}
