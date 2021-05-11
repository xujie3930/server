package com.szmsd.common.core.interceptor;

import com.alibaba.fastjson.JSON;
import com.szmsd.common.core.config.TokenConfig;
import com.szmsd.common.core.domain.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zhangyuyuan
 * @date 2021-05-10 11:20
 */
public class TokenHandlerInterceptor implements HandlerInterceptor {
    private final Logger logger = LoggerFactory.getLogger(TokenHandlerInterceptor.class);

    private final TokenVerification tokenVerification;

    public TokenHandlerInterceptor(RedisTemplate<Object, Object> redisTemplate, TokenConfig tokenConfig) {
        this.tokenVerification = new TokenVerificationAdapter(redisTemplate, tokenConfig);
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        try {
            return this.tokenVerification.handler(request, response);
        } catch (TokenException e) {
            logger.error(e.getMessage(), e);
            ResponseUtil.setHeader(response, "T-T-E", "resubmit");
            ResponseUtil.responseWrite(response, JSON.toJSONString(R.failed(e.getMessage())));
            return false;
        }
    }

    @Override
    public void postHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) throws Exception {
        TokenContext.destroy();
    }
}
