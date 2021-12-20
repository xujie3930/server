package com.szmsd.http.config;

import com.szmsd.http.servlet.RequestForwardServlet;
import com.szmsd.http.servlet.matcher.RequestForwardMatcher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangyuyuan
 * @date 2021-04-30 11:32
 */
@ConditionalOnExpression("${com.szmsd.rmi.enabled:false}")
@Configuration
public class ServletConfig {

    private final RequestForwardMatcher requestForwardMatcher;

    public ServletConfig(RequestForwardMatcher requestForwardMatcher) {
        this.requestForwardMatcher = requestForwardMatcher;
    }

    @Bean
    public ServletRegistrationBean<RequestForwardServlet> registerRequestForwardServlet() {
        ServletRegistrationBean<RequestForwardServlet> requestForwardServlet = new ServletRegistrationBean<>();
        requestForwardServlet.setServlet(new RequestForwardServlet(requestForwardMatcher));
        requestForwardServlet.addUrlMappings("/rmi");
        return requestForwardServlet;
    }

}
