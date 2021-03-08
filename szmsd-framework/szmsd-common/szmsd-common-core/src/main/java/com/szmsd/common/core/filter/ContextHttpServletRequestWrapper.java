package com.szmsd.common.core.filter;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ContextHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private final byte[] body;
    // 用于将流保存下来
    private ContextServletInputStream contextServletInputStream;

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request The request to wrap
     * @throws IllegalArgumentException if the request is null
     */
    public ContextHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        body = HttpHelper.getBodyString(request).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new ContextServletInputStream(body);
    }

    public ContextServletInputStream getContextServletInputStream() {
        return new ContextServletInputStream(body);
    }
}
