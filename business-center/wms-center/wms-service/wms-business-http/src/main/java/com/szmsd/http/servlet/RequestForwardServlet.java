package com.szmsd.http.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author zhangyuyuan
 * @date 2021-04-30 11:33
 */
public class RequestForwardServlet extends HttpServlet {
    private final Logger logger = LoggerFactory.getLogger(RequestForwardServlet.class);

    private final PathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public void init() throws ServletException {
        logger.info("初始化");
        super.init();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String uri = req.getRequestURI();

        super.service(req, resp);
    }

    @Override
    public void destroy() {
        logger.info("销毁");
        super.destroy();
    }
}
