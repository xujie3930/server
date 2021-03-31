package com.szmsd.bas.driver;


import com.szmsd.bas.service.InitRedisCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Configuration
@Slf4j
public class InitRedisCache {

    @Resource
    private InitRedisCacheService initRedisCacheService;

    @PostConstruct
    public void loadWarehouse() {
        initRedisCacheService.updateWarehouseRedis();
    }
}
