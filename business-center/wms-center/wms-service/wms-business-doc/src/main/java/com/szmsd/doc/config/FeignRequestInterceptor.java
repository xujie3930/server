package com.szmsd.doc.config;

import com.szmsd.doc.utils.AuthenticationUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@Component
public class FeignRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        // 这里不能强制设置Accept和Content-Type，不然通过feign的方式调附件上传接口会丢失file
        // template.header("Accept", MediaType.ALL_VALUE);
        // template.header("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);
        // 获取header中的属性
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return;
        }
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                Enumeration<String> values = request.getHeaders(name);
                while (values.hasMoreElements()) {
                    String value = values.nextElement();
                    // 停止传播
                    if ("T-Token-Verification".equalsIgnoreCase(name)) {
                        break;
                    }
                    if ("content-type".equalsIgnoreCase(name) && value.contains("multipart/form-data")) {
                        break;
                    }
                    template.header(name, value);
                }
            }
        }
        // 追加header
        String requestId = MDC.get("TID");
        if (StringUtils.isNotBlank(requestId)) {
            template.header("X-TID", requestId);
        }

    }


}
