package com.szmsd.auth.config;

import com.szmsd.common.core.constant.CacheConstants;
import com.szmsd.common.core.constant.HttpStatus;
import com.szmsd.common.core.constant.SecurityConstants;
import com.szmsd.common.security.domain.LoginUser;
import com.szmsd.common.security.handler.CustomWebResponseExceptionTranslator;
import com.szmsd.common.security.service.RedisClientDetailsService;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenGranter;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * OAuth2 认证服务配置
 *
 * @author szmsd
 */
@Configuration
@EnableAuthorizationServer
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter
{
    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private DataSource dataSource;

    @Resource
    private RedisConnectionFactory redisConnectionFactory;

    @Resource
    private UserDetailsService userDetailsService;

    @Resource
    private TokenEnhancer tokenEnhancer;

    @Resource
    private ThirdAuthenticationConfig thirdAuthenticationConfig;

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
        return restTemplate;
    }

    /**
     * 定义授权和令牌端点以及令牌服务
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        ClientDetailsService clientDetails = endpoints.getClientDetailsService();
        AuthorizationServerTokenServices tokenServices = endpoints.getTokenServices();
        AuthorizationCodeServices authorizationCodeServices = endpoints.getAuthorizationCodeServices();
        OAuth2RequestFactory requestFactory = endpoints.getOAuth2RequestFactory();
        List<TokenGranter> tokenGranters = new ArrayList<TokenGranter>();
        tokenGranters.add(new AuthorizationCodeTokenGranter(tokenServices, authorizationCodeServices, clientDetails,
                requestFactory));
        tokenGranters.add(new RefreshTokenGranter(tokenServices, clientDetails, requestFactory));
        ImplicitTokenGranter implicit = new ImplicitTokenGranter(tokenServices, clientDetails, requestFactory);
        tokenGranters.add(implicit);
        tokenGranters.add(new ClientCredentialsTokenGranter(tokenServices, clientDetails, requestFactory));
        ThirdResourceOwnerPasswordTokenGranter thirdResourceOwnerPasswordTokenGranter = new ThirdResourceOwnerPasswordTokenGranter(authenticationManager, tokenServices, clientDetails, requestFactory);
        thirdResourceOwnerPasswordTokenGranter.setThirdAuthenticationConfig(thirdAuthenticationConfig);
        thirdResourceOwnerPasswordTokenGranter.setRestTemplate(restTemplate());
        thirdResourceOwnerPasswordTokenGranter.setAuthenticationManager(authenticationManager);
        tokenGranters.add(thirdResourceOwnerPasswordTokenGranter);
        if (authenticationManager != null) {
            tokenGranters.add(new ResourceOwnerPasswordTokenGranter(authenticationManager, tokenServices,
                    clientDetails, requestFactory));
        }
        ThirdCompositeTokenGranter thirdCompositeTokenGranter = new ThirdCompositeTokenGranter(tokenGranters);
        endpoints
                // 请求方式
                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST)
                // 指定token存储位置
                .tokenStore(tokenStore())
                // 自定义生成令牌
                .tokenEnhancer(tokenEnhancer)
                // 用户账号密码认证
                .userDetailsService(userDetailsService)
                // 指定认证管理器
                .authenticationManager(authenticationManager)
                .tokenGranter(thirdCompositeTokenGranter)
                // 是否重复使用 refresh_token
                .reuseRefreshTokens(true)
                .addInterceptor(new DocHandlerInterceptor())
                // 自定义异常处理
                .exceptionTranslator(new CustomWebResponseExceptionTranslator());
    }

    /**
     * 配置令牌端点(Token Endpoint)的安全约束
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer)
    {
        oauthServer.allowFormAuthenticationForClients()
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("permitAll()")
                .allowFormAuthenticationForClients();
    }

    /**
     * 声明 ClientDetails实现
     */
    public RedisClientDetailsService clientDetailsService()
    {
        RedisClientDetailsService clientDetailsService = new RedisClientDetailsService(dataSource);
        return clientDetailsService;
    }

    /**
     * 配置客户端详情
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception
    {
        clients.withClientDetails(clientDetailsService());
    }

    /**
     * 基于 Redis 实现，令牌保存到缓存
     */
    @Bean
    public TokenStore tokenStore()
    {
        RedisTokenStore tokenStore = new RedisTokenStore(redisConnectionFactory);
        tokenStore.setPrefix(CacheConstants.OAUTH_ACCESS);
        return tokenStore;
    }

    /**
     * 自定义生成令牌
     */
    @Bean
    public TokenEnhancer tokenEnhancer()
    {
        return new TokenEnhancer()
        {
            @Override
            public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication)
            {
                if (accessToken instanceof DefaultOAuth2AccessToken)
                {
                    DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) accessToken;
                    Authentication userAuthentication = authentication.getUserAuthentication();
                    if (userAuthentication instanceof UsernamePasswordAuthenticationToken) {
                        LoginUser user = (LoginUser) userAuthentication.getPrincipal();
                        Map<String, Object> additionalInformation = new LinkedHashMap<String, Object>();
                        additionalInformation.put(SecurityConstants.DETAILS_USERNAME, authentication.getName());
                        additionalInformation.put(SecurityConstants.DETAILS_USER_ID, user.getUserId());
                        additionalInformation.put("code", HttpStatus.SUCCESS);
                        additionalInformation.put("sellerCode", user.getSellerCode());
                        additionalInformation.put("allDataScope", user.isAllDataScope());
                        additionalInformation.put("permissions", user.getPermissions());
                        token.setAdditionalInformation(additionalInformation);
                    } else if (userAuthentication instanceof ThirdLoginAuthenticationToken) {
                        Map<String, Object> additionalInformation = new LinkedHashMap<String, Object>();
                        additionalInformation.put(SecurityConstants.DETAILS_USERNAME, authentication.getPrincipal());
                        additionalInformation.put(SecurityConstants.DETAILS_USER_ID, 0);
                        additionalInformation.put("code", HttpStatus.SUCCESS);
                        token.setAdditionalInformation(additionalInformation);
                    }
                }
                return accessToken;
            }
        };
    }
}
