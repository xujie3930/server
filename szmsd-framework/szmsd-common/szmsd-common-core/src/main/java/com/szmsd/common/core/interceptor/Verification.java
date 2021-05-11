package com.szmsd.common.core.interceptor;

import com.szmsd.common.core.config.TokenConfig;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangyuyuan
 * @date 2021-05-10 17:05
 */
public interface Verification {

    /**
     * 验证token
     *
     * @param request          request
     * @param verificationEnum verificationEnum
     * @param token            token
     * @throws TokenException TokenException
     */
    void verify(HttpServletRequest request, VerificationEnum verificationEnum, String token) throws TokenException;

    /**
     * 验证逻辑
     */
    abstract class AbstractVerification implements Verification {
        private final RedisTemplate<Object, Object> redisTemplate;
        private final OnlyKey onlyKey;
        private final Duration time;

        public AbstractVerification(RedisTemplate<Object, Object> redisTemplate, OnlyKey onlyKey, Duration time) {
            this.redisTemplate = redisTemplate;
            this.onlyKey = onlyKey;
            this.time = time;
        }

        @Override
        public void verify(HttpServletRequest request, VerificationEnum verificationEnum, String token) throws TokenException {
            String onlyKey = this.onlyKey.handler(request.getMethod(), request.getRequestURI(), verificationEnum, token);
            if (handler(onlyKey)) {
                throw new TokenException("请勿重复提交");
            }
            TokenContext.get().setOnlyKey(onlyKey);
            this.redisTemplate.opsForValue().set(onlyKey, "", this.time.get(ChronoUnit.SECONDS), TimeUnit.SECONDS);
        }

        public boolean hasKey(String key) {
            Boolean aBoolean = this.redisTemplate.hasKey(key);
            if (null == aBoolean) {
                return false;
            }
            return aBoolean;
        }

        abstract boolean handler(String key);

        public RedisTemplate<Object, Object> getRedisTemplate() {
            return redisTemplate;
        }

        public OnlyKey getOnlyKey() {
            return onlyKey;
        }

        public Duration getTime() {
            return time;
        }
    }

    /**
     * 令牌验证
     */
    class TokenVerification extends AbstractVerification {

        public TokenVerification(RedisTemplate<Object, Object> redisTemplate, TokenConfig tokenConfig) {
            super(redisTemplate, new OnlyKey.Md5OnlyKey(tokenConfig.getName()), tokenConfig.getTime());
        }

        @Override
        boolean handler(String key) {
            return super.hasKey(key);
        }
    }
}
