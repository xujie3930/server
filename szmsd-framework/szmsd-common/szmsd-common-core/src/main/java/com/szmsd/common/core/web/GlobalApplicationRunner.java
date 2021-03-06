package com.szmsd.common.core.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.core.SpringVersion;
import org.springframework.stereotype.Component;

/**
 * @author zhangyuyuan
 * @date 2020-07-02 002 19:40
 */
@Component
public class GlobalApplicationRunner implements ApplicationRunner {

    private Logger logger = LoggerFactory.getLogger(GlobalApplicationRunner.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        String name = applicationContext.getEnvironment().getProperty("spring.application.name");
        String active = applicationContext.getEnvironment().getProperty("spring.profiles.active");
        String port = applicationContext.getEnvironment().getProperty("server.port");
        String contextPath = applicationContext.getEnvironment().getProperty("server.servlet.context-path");

        this.line();
        this.logger.info("project started successfully!");
        this.logger.info("name: {}, active: {}, port: {}, context-path: {}", name, active, port, contextPath);
        this.line();
        this.logger.info("SpringVersion: {}", SpringVersion.getVersion());
    }

    public void line() {
        this.logger.info("--------------------------------------------");
    }
}
