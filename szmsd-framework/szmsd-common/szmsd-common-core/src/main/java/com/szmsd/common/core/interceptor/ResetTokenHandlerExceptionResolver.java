package com.szmsd.common.core.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zhangyuyuan
 * @date 2021-05-11 10:20
 */
public class ResetTokenHandlerExceptionResolver implements HandlerExceptionResolver {

    private final RedisTemplate<Object, Object> redisTemplate;

    public ResetTokenHandlerExceptionResolver(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public ModelAndView resolveException(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, Object handler, @NonNull Exception ex) {
        TokenContext tokenContext = TokenContext.get();
        if (null != tokenContext) {
            String onlyKey = tokenContext.getOnlyKey();
            if (StringUtils.isNotEmpty(onlyKey)) {
                this.redisTemplate.delete(onlyKey);
            }
        }
        return null;
    }
}
