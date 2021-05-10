package com.szmsd.common.core.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangyuyuan
 * @date 2021-05-10 11:38
 */
public final class ResponseUtil {
    private final static Logger logger = LoggerFactory.getLogger(ResponseUtil.class);

    /**
     * 设置 header
     *
     * @param response response
     * @param key      key
     * @param value    value
     */
    public static void setHeader(HttpServletResponse response, String key, String value) {
        response.setHeader(key, value);
    }

    /**
     * 从response输出对象
     *
     * @param response response
     * @param text     text
     */
    public static void responseWrite(HttpServletResponse response, String text) {
        reset(response);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
        PrintWriter pw;
        try {
            pw = response.getWriter();
            pw.write(text);
            pw.flush();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public static void reset(HttpServletResponse response) {
        Map<String, String> headers = new HashMap<>();
        Collection<String> headerNames = response.getHeaderNames();
        if (null != headerNames) {
            for (String headerName : headerNames) {
                headers.put(headerName, response.getHeader(headerName));
            }
        }
        response.reset();
        if (!headers.isEmpty()) {
            headers.forEach(response::addHeader);
        }
    }
}
