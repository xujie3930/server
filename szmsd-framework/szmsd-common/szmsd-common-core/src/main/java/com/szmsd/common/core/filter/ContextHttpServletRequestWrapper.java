package com.szmsd.common.core.filter;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;

public class ContextHttpServletRequestWrapper extends HttpServletRequestWrapper {
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
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (null == contextServletInputStream) {
            contextServletInputStream = new ContextServletInputStream(super.getInputStream());
        }
        return contextServletInputStream;
    }

    public ContextServletInputStream getContextServletInputStream() {
        return contextServletInputStream;
    }
}
