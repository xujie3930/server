package com.szmsd.http.config;

import com.szmsd.http.servlet.RequestForwardServlet;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangyuyuan
 * @date 2021-04-30 11:32
 */
@ConditionalOnExpression("${com.szmsd.rfs.enabled:false}")
@Configuration
public class ServletConfig {

    @Bean
    public ServletRegistrationBean<RequestForwardServlet> registerRequestForwardServlet() {
        ServletRegistrationBean<RequestForwardServlet> requestForwardServlet = new ServletRegistrationBean<>();
        requestForwardServlet.setServlet(new RequestForwardServlet());
        requestForwardServlet.addUrlMappings("/rf");
        return requestForwardServlet;
    }
}
