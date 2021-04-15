package com.szmsd.http.config;

import com.szmsd.http.service.IHtpConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author zhangyuyuan
 * @date 2021-04-13 20:51
 */
@Slf4j
@Component
@Configuration
public class HttpApplicationRunner {

    @Resource
    private IHtpConfigService htpConfigService;

    @PostConstruct
    public void initHttpConfig() {
        log.info("加载外部接口服务配置 ------------- start");
        htpConfigService.loadHtpConfig();
        log.info("加载外部接口服务配置 ------------- end");
    }

}
