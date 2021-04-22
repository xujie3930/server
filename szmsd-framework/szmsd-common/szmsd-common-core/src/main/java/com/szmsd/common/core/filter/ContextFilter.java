package com.szmsd.common.core.filter;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.util.WebUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author zhangyuyuan
 * @date 2020-11-20 020 16:41
 */
public class ContextFilter implements Filter {

    private static final int CONTENT_LENGTH = 40960;
    private static final String METHOD_GET = "GET";
    private final Logger logger = LoggerFactory.getLogger(ContextFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("ContextFilter.init");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // requestId
        String requestId = MDC.get("TID");
        if (StringUtils.isBlank(requestId)) {
            requestId = UUIDUtil.uuid();
            MDC.put("TID", requestId);
        }

        String uri = request.getRequestURI();
        String method = request.getMethod();
        long startTime = System.currentTimeMillis();

        ContextHttpServletRequestWrapper requestWrapper = new ContextHttpServletRequestWrapper(request);
        ContextHttpServletResponseWrapper responseWrapper = new ContextHttpServletResponseWrapper(response);

        filterChain.doFilter(requestWrapper, responseWrapper);

        if (logger.isInfoEnabled()) {
            String requestBody = requestParam(requestWrapper);
            String responseBody = responseParam(responseWrapper);

            StringBuilder builder = new StringBuilder();
            if (StringUtil.isEmpty(requestBody)) {
                requestBody = "{}";
            } else {
                requestBody = requestBody.replaceAll("\t", "")
                        .replaceAll(" ", "")
                        .replaceAll("\n", "")
                        .replaceAll("\r", "");
            }
            if (StringUtil.isEmpty(responseBody)) {
                responseBody = "{}";
            } else {
                responseBody = responseBody.replaceAll("\t", "")
                        .replaceAll(" ", "")
                        .replaceAll("\n", "")
                        .replaceAll("\r", "");
            }
            builder.append("{\"requestBody\":").append(requestBody).append(",");
            builder.append("\"responseBody\":").append(responseBody).append("}");
            logger.info(builder.toString());
        }

        long endTime = System.currentTimeMillis();
        long times = (endTime - startTime);
        if (logger.isInfoEnabled()) {
            logger.info(">>>请求路径：{}[{}]，开始时间：{}，结束时间：{}，总耗时：{}", uri, method, startTime, endTime, times);
        }
    }

    private String requestParam(ContextHttpServletRequestWrapper requestWrapper) {
        String body = "";
        String method = requestWrapper.getMethod();
        // 处理GET请求参数
        if (method.equalsIgnoreCase(METHOD_GET)) {
            body = requestWrapper.getQueryString();
        } else {
            // 处理POST请求参数
            int size = requestWrapper.getContentLength();
            if (size < CONTENT_LENGTH) {
                ContextServletInputStream contextServletInputStream = requestWrapper.getContextServletInputStream();
                // 1.这里无法读取到流的信息，就直接获取request中的参数就行了
                // 2.虽然修改了Filter的顺序，或者是重写HiddenHttpMethodFilter，也无法读取到流
                if (contextServletInputStream != null) {
                    body = new String(contextServletInputStream.getContent().getBytes(), StandardCharsets.UTF_8);
                } else {
                    Map<String, Object> objectMap = WebUtils.getParametersStartingWith(requestWrapper, null);
                    body = JSON.toJSONString(objectMap);
                }
            }
        }
        return body;
    }

    private String responseParam(ContextHttpServletResponseWrapper responseWrapper) {
        ContextServletOutputStream traceOutputStream = responseWrapper.getContextServletOutputStream();
        if (null != traceOutputStream && StringUtil.isNotEmpty(traceOutputStream.getContent())) {
            return new String(traceOutputStream.getContent().getBytes(), StandardCharsets.UTF_8);
        }
        return null;
    }

    @Override
    public void destroy() {
        logger.info("ContextFilter.destroy");
    }
}
