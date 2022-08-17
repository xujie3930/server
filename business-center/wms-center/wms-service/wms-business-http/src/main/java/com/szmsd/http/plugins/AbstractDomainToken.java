package com.szmsd.http.plugins;

import com.alibaba.fastjson.JSON;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.http.config.DomainTokenValue;
import com.szmsd.http.utils.RedirectUriUtil;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

public abstract class AbstractDomainToken implements DomainToken {
    protected final Logger logger = LoggerFactory.getLogger(AbstractDomainToken.class);

    protected String domain;
    protected DomainTokenValue domainTokenValue;
    @Autowired
    protected RedisTemplate<Object, Object> redisTemplate;
    @Value("${spring.application.name}")
    protected String applicationName;
    @Autowired
    protected RedissonClient redissonClient;

    @Override
    public String getTokenValue() {
        logger.info(">>>[获取Token]，1.开始获取Token");
        // 获取access token信息
        String accessToken = this.getAccessToken();
        logger.info(">>>[获取Token]，2.获取Token值：{}", accessToken);
        if (StringUtils.isNotEmpty(accessToken)) {
            return accessToken;
        }
        // 上锁，进行阻塞
        String lockKey = applicationName + ":DomainToken:getTokenValue:" + this.domainTokenValue.getDomainToken();
        RLock lock = this.redissonClient.getLock(lockKey);
        try {
            // 锁超时60秒
            if (lock.tryLock(60, TimeUnit.SECONDS)) {
                logger.info(">>>[获取Token]，3.获取到锁");
                // 二次验证
                accessToken = this.getAccessToken();
                logger.info(">>>[获取Token]，4.重新获取Token值：{}", accessToken);
                if (StringUtils.isNotEmpty(accessToken)) {
                    return accessToken;
                }
                // 获取refresh token，根据refresh token去重新获取 token 信息
                String refreshTokenKey = this.refreshTokenKey();
                String wrapRefreshTokenKey = RedirectUriUtil.wrapRefreshTokenKey(refreshTokenKey);
                Object refreshTokenObject = this.redisTemplate.opsForValue().get(wrapRefreshTokenKey);
                logger.info(">>>[获取Token]，5.获取RefreshToken值：{}，Redis键值：{}", refreshTokenObject, wrapRefreshTokenKey);
                if (null == refreshTokenObject) {
                    refreshTokenObject = this.getRefreshToken();
                    logger.info(">>>[获取Token]，5.1获取RefreshToken值：{}", refreshTokenObject);
                }
                if (null != refreshTokenObject) {
                    String refreshToken = String.valueOf(refreshTokenObject);
                    TokenValue tokenValue = this.internalGetTokenValueByRefreshToken(refreshToken);
                    logger.info(">>>[获取Token]，6.根据RefreshToken获取Token信息：{}", JSON.toJSONString(tokenValue));
                    if (null != tokenValue) {
                        this.setTokenValue(tokenValue);
                        return tokenValue.getAccessToken();
                    }
                }
                // 直接获取token信息
                TokenValue tokenValue = this.internalGetTokenValue();
                logger.info(">>>[获取Token]，7.根据Token接口获取Token信息：{}", JSON.toJSONString(tokenValue));
                if (null != tokenValue) {
                    this.setTokenValue(tokenValue);
                    return tokenValue.getAccessToken();
                }
            }
        } catch (Exception e) {
            this.logger.error(">>>[获取Token]，8.获取Token失败，" + e.getMessage(), e);
            throw new CommonException("999", "获取Token失败，" + e.getMessage());
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        throw new CommonException("999", "获取Token失败");
    }

    private void setTokenValue(TokenValue tokenValue) {
        Long expiresIn = tokenValue.getExpiresIn();
        if (null == expiresIn) {
            // 1天
            expiresIn = this.domainTokenValue.getAccessTokenExpiresIn();
        }
        // 时间兼容，30分钟（30分钟 * 60秒）
        expiresIn -= 1800L;
        String accessTokenKey = this.accessTokenKey();
        String wrapAccessTokenKey = RedirectUriUtil.wrapAccessTokenKey(accessTokenKey);
        this.redisTemplate.opsForValue().set(wrapAccessTokenKey, tokenValue.getAccessToken(), expiresIn, TimeUnit.SECONDS);
        String refreshTokenKey = this.refreshTokenKey();
        String wrapRefreshTokenKey = RedirectUriUtil.wrapRefreshTokenKey(refreshTokenKey);
        long refreshTokenExpiresIn = this.domainTokenValue.getRefreshTokenExpiresIn();
        this.redisTemplate.opsForValue().set(wrapRefreshTokenKey, tokenValue.getRefreshToken(), refreshTokenExpiresIn, TimeUnit.SECONDS);
        logger.info("授权后获取token的信息：{}",tokenValue);
    }

    private String getAccessToken() {
        String accessTokenKey = this.accessTokenKey();
        String wrapAccessTokenKey = RedirectUriUtil.wrapAccessTokenKey(accessTokenKey);
        Object accessTokenObject = this.redisTemplate.opsForValue().get(wrapAccessTokenKey);
        if (null != accessTokenObject) {
            return String.valueOf(accessTokenObject);
        }
        return null;
    }

    @Override
    public String accessTokenKey() {
        return this.domainTokenValue.getDomainToken();
    }

    @Override
    public String refreshTokenKey() {
        return this.domainTokenValue.getDomainToken();
    }

    /**
     * 获取refreshToken
     *
     * @return refreshToken
     */
    public String getRefreshToken() {
        return null;
    }

    /**
     * 通过授权校验码，获取 token value
     *
     * @return TokenValue
     */
    abstract TokenValue internalGetTokenValue();

    /**
     * 通过 refresh token，获取 token value
     *
     * @param refreshToken refresh token
     * @return TokenValue
     */
    abstract TokenValue internalGetTokenValueByRefreshToken(String refreshToken);

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public DomainTokenValue getDomainTokenValue() {
        return domainTokenValue;
    }

    public void setDomainTokenValue(DomainTokenValue domainTokenValue) {
        this.domainTokenValue = domainTokenValue;
    }
}
