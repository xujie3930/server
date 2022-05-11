package com.szmsd.bas.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = ShopifyAppConfig.CONFIG_PREFIX)
public class ShopifyAppConfig {
    public static final String CONFIG_PREFIX = "shopify.app";

    private String clientId;
    private String clientSecret;

    // https://shopify.dev/apps/auth/oauth/getting-started#step-3-ask-for-permission
    // https://{shop}.myshopify.com/admin/oauth/authorize?client_id={api_key}&scope={scopes}&redirect_uri={redirect_uri}&state={nonce}&grant_options[]={access_mode}
    private String oauthAuthorize;
    // read_orders,write_orders,read_locations,read_shipping,write_shipping
    private String scope;
    // ck1登录地址
    private String redirectUri;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getOauthAuthorize() {
        return oauthAuthorize;
    }

    public void setOauthAuthorize(String oauthAuthorize) {
        this.oauthAuthorize = oauthAuthorize;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }
}
