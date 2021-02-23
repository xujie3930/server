package com.szmsd.common.security.handler;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.constant.HttpStatus;
import com.szmsd.common.core.domain.R;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description: OAuth2 ---token过期
 * @Author
 * @Date
 **/
@Component
public class TokenExceptionEntryPoint extends OAuth2AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {

        httpServletResponse.setContentType("application/json;charset=UTF-8");
        // TODO  new throw
        httpServletResponse.getWriter().write(JSONObject.toJSONString(R.failed(HttpStatus.FORBIDDEN, "token无效或已过期！")));
    }
}
