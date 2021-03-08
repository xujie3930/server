package com.szmsd.open.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.domain.R;
import com.szmsd.open.config.AuthConfig;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.UUID;

/**
 * @author zhangyuyuan
 * @date 2021-03-06 15:12
 */
@Component
public class AuthHandlerInterceptor implements HandlerInterceptor {
    private final Logger logger = LoggerFactory.getLogger(AuthHandlerInterceptor.class);

    @Autowired
    private AuthConfig authConfig;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        String uri = request.getRequestURI();
        logger.info("{} 请求处理开始", uri);
        // requestId
        String requestId = MDC.get("traceId");
        if (StringUtils.isBlank(requestId)) {
            requestId = UUID.randomUUID().toString().replace("-", "");
        }
        MDC.put("TID", requestId);
        // 从请求头上获取
        String userId = request.getParameter(RequestConstant.USER_ID);
        if (StringUtils.isEmpty(userId)) {
            userId = request.getHeader(RequestConstant.USER_ID);
        }
        String password = request.getParameter(RequestConstant.PASSWORD);
        if (StringUtils.isEmpty(password)) {
            password = request.getHeader(RequestConstant.PASSWORD);
        }
        // 判断有没有配置
        if (StringUtils.isNotEmpty(authConfig.getUserId()) && StringUtils.isNotEmpty(authConfig.getPassword())) {
            if (!(authConfig.getUserId().equals(userId) && authConfig.getPassword().equals(password))) {
                logger.info("认证失败");
                response.reset();
                response.setCharacterEncoding(RequestConstant.ENCODING);
                response.setContentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8");
                PrintWriter pw = response.getWriter();
                pw.write(JSONObject.toJSONString(R.failed("认证失败")));
                pw.flush();
                pw.close();
                return false;
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) throws Exception {
        String uri = request.getRequestURI();
        logger.info("{} 请求处理完成", uri);
    }
}
