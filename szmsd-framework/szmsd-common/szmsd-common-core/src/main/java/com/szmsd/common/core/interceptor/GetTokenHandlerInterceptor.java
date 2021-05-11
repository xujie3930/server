package com.szmsd.common.core.interceptor;

import com.alibaba.fastjson.JSON;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.filter.UUIDUtil;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zhangyuyuan
 * @date 2021-05-10 18:05
 */
public class GetTokenHandlerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        R<String> r = R.ok(UUIDUtil.uuid());
        ResponseUtil.responseWrite(response, JSON.toJSONString(r));
        return false;
    }
}
