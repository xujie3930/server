package com.szmsd.auth.config;

import com.szmsd.common.core.constant.SecurityConstants;
import com.szmsd.common.core.utils.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DocHandlerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        String client_id = request.getParameter("client_id");
        // /oauth/authorize
        String response_type = request.getParameter("response_type");
        // /oauth/token
        String grant_type = request.getParameter("grant_type");
        if (StringUtils.isNotEmpty(client_id)
                && "doc".equals(client_id)
                && ("code".equals(response_type) || "refresh_token".equals(grant_type))) {
            request.getSession().setAttribute(SecurityConstants.DETAILS_USER_TYPE, SecurityConstants.DETAILS_USER_TYPE_CLIENT);
        }
        return true;
    }
}
