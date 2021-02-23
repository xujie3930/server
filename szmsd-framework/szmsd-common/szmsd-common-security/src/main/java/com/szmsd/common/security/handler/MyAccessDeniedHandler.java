package com.szmsd.common.security.handler;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.domain.R;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description: 处理权限（权限不足）
 * @Author
 * @Date
 **/
@Component
public class MyAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {

        httpServletResponse.setContentType("application/json;charset=UTF-8");
        // TODO  new throw
        httpServletResponse.getWriter().write(JSONObject.toJSONString(R.failed(401, "权限不足，不允许访问！")));
    }
}
