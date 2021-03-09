package com.szmsd.common.core.filter;

import com.github.pagehelper.util.StringUtil;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;

public class ContextHttpServletResponseWrapper extends HttpServletResponseWrapper {

    private static final String METHOD_GET = "GET";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String REQUEST_ACCEPT_CONTENT_TYPE = "application/x-www-form-urlencoded,application/json,text/xml";
    private static final String RESPONSE_ACCEPT_CONTENT_TYPE = "text/xml,text/plain,application/json";
    private static final String SEPARATOR_STR = ",";

    private ContextServletOutputStream contextServletOutputStream;
    private HttpServletResponse response;

    /**
     * Constructs a response adaptor wrapping the given response.
     *
     * @param response The response to be wrapped
     * @throws IllegalArgumentException if the response is null
     */
    public ContextHttpServletResponseWrapper(HttpServletResponse response) {
        super(response);
        this.response = response;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return this.response.getWriter();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        // 如果响应头的ContentType不符合规则则直接返回
        if (!shouldTracer(null, response)) {
            return super.getOutputStream();
        }
        // 拦截响应流,获取响应body
        if (null == contextServletOutputStream) {
            contextServletOutputStream = new ContextServletOutputStream(super.getOutputStream());
        }
        return contextServletOutputStream;
    }

    public ContextServletOutputStream getContextServletOutputStream() {
        return contextServletOutputStream;
    }

    /**
     * 判断是否包装过滤请求
     * 当request时只tracer请求体类型为application/x-www-form-urlencoded,application/json,text/xml的请求
     * 当response时只tracer响应体类型为text/xml,text/plain,application/json的响应
     *
     * @param request  request
     * @param response response
     * @return boolean
     */
    public boolean shouldTracer(HttpServletRequest request, HttpServletResponse response) {
        boolean tracerFlag = false;
        if (null != request) {
            // get不处理
            if (METHOD_GET.equalsIgnoreCase(request.getMethod())) {
                return true;
            }
            String requestContentType = request.getHeader(CONTENT_TYPE);
            // 判断请求头的CONTENT_TYPE是否符合规则
            if (StringUtil.isNotEmpty(requestContentType)) {
                String[] acceptContentTypes = REQUEST_ACCEPT_CONTENT_TYPE.split(SEPARATOR_STR);
                if (hasContentType(requestContentType, acceptContentTypes)) {
                    return true;
                }
            }
        }

        if (null != response) {
            String responseContentType = response.getContentType();
            // 判断响应头的CONTENT_TYPE是否符合规则
            if (StringUtil.isNotEmpty(responseContentType)) {
                String[] acceptContentTypes = RESPONSE_ACCEPT_CONTENT_TYPE.split(SEPARATOR_STR);
                if (hasContentType(responseContentType, acceptContentTypes)) {
                    return true;
                }
            }
        }
        return tracerFlag;
    }

    /**
     * 验证是否包含
     *
     * @param contentType        contentType
     * @param acceptContentTypes acceptContentTypes
     * @return boolean
     */
    private boolean hasContentType(String contentType, String[] acceptContentTypes) {
        for (String acceptContentType : acceptContentTypes) {
            if (contentType.contains(acceptContentType)) {
                return true;
            }
        }
        return false;
    }
}
