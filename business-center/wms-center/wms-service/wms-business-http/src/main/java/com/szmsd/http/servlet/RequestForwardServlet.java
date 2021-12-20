package com.szmsd.http.servlet;

import com.alibaba.fastjson.JSON;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.filter.ContextHttpServletRequestWrapper;
import com.szmsd.common.core.filter.ContextHttpServletResponseWrapper;
import com.szmsd.common.core.filter.ContextServletInputStream;
import com.szmsd.common.core.filter.ContextServletOutputStream;
import com.szmsd.http.servlet.matcher.RequestForwardMatcher;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.web.header.HeaderWriterFilter;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author zhangyuyuan
 * @date 2021-04-30 11:33
 */
public class RequestForwardServlet extends HttpServlet {
    private final Logger logger = LoggerFactory.getLogger(RequestForwardServlet.class);

    private final PathMatcher pathMatcher = new AntPathMatcher();
    private final RequestForwardMatcher requestForwardMatcher;

    public RequestForwardServlet(RequestForwardMatcher requestForwardMatcher) {
        this.requestForwardMatcher = requestForwardMatcher;
    }

    @Override
    public void init() throws ServletException {
        logger.info("初始化");
        super.init();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ContextHttpServletRequestWrapper requestWrapper = new ContextHttpServletRequestWrapper(req);
        ContextHttpServletResponseWrapper responseWrapper = new ContextHttpServletResponseWrapper(resp);

        String requestBody = requestBody(requestWrapper);

        logger.info("requestBody:{}", requestBody);

        String uri = req.getRequestURI();

        if (this.requestForwardMatcher.match(uri)) {
            logger.info("匹配到规则");
        }

        R<Object> r = R.ok();

        // this.responseWrite(responseWrapper, r);
        ServletOutputStream servletOutputStream = resp.getOutputStream();
        servletOutputStream.write(JSON.toJSONBytes(r));
    }

    private void responseWrite(ContextHttpServletResponseWrapper responseWrapper, R<?> r) {
        responseWrapper.reset();
        responseWrapper.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        responseWrapper.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=" + StandardCharsets.UTF_8);
        try {
            ContextServletOutputStream contextServletOutputStream = (ContextServletOutputStream) responseWrapper.getOutputStream();
            IOUtils.write(JSON.toJSONBytes(r), contextServletOutputStream);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private String requestBody(ContextHttpServletRequestWrapper requestWrapper) {
        String body;
        String method = requestWrapper.getMethod();
        // 处理GET请求参数
        if (method.equalsIgnoreCase("GET")) {
            body = requestWrapper.getQueryString();
        } else {
            // 处理POST请求参数
            ContextServletInputStream contextServletInputStream = requestWrapper.getContextServletInputStream();
            // 1.这里无法读取到流的信息，就直接获取request中的参数就行了
            // 2.虽然修改了Filter的顺序，或者是重写HiddenHttpMethodFilter，也无法读取到流
            if (null == contextServletInputStream) {
                try {
                    contextServletInputStream = (ContextServletInputStream) requestWrapper.getInputStream();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            if (contextServletInputStream != null) {
                body = new String(contextServletInputStream.getContent().getBytes(), StandardCharsets.UTF_8);
            } else {
                Map<String, Object> objectMap = WebUtils.getParametersStartingWith(requestWrapper, null);
                body = JSON.toJSONString(objectMap);
            }
        }
        if (null != body) {
            body = body.replaceAll("\t", "")
                    .replaceAll(" ", "")
                    .replaceAll("\n", "")
                    .replaceAll("\r", "");
        }
        return body;
    }

    @Override
    public void destroy() {
        logger.info("销毁");
        super.destroy();
    }
}
