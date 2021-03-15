package com.szmsd.open.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.domain.R;
import com.szmsd.open.config.AuthConfig;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @author zhangyuyuan
 * @date 2021-03-06 15:12
 */
@Component
public class AuthHandlerInterceptor implements HandlerInterceptor, Ordered {
    private final Logger logger = LoggerFactory.getLogger(AuthHandlerInterceptor.class);

    @Autowired
    private AuthConfig authConfig;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        // 从请求头上获取
        String appId = request.getParameter(RequestConstant.APP_ID);
        if (StringUtils.isEmpty(appId)) {
            appId = request.getHeader(RequestConstant.APP_ID);
        }
        String sign = request.getParameter(RequestConstant.SIGN);
        if (StringUtils.isEmpty(sign)) {
            sign = request.getHeader(RequestConstant.SIGN);
        }
        // 判断有没有配置
        if (StringUtils.isNotEmpty(authConfig.getAppId()) && StringUtils.isNotEmpty(authConfig.getSign())) {
            if (!(authConfig.getAppId().equals(appId) && authConfig.getSign().equals(sign))) {
                logger.info("认证失败");
                response.reset();
                response.setCharacterEncoding(RequestConstant.ENCODING);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
                PrintWriter pw = response.getWriter();
                pw.write(JSONObject.toJSONString(R.failed("认证失败")));
                pw.flush();
                // pw.close();
                return false;
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) throws Exception {
    }

    @Override
    public int getOrder() {
        return 1000;
    }
}
