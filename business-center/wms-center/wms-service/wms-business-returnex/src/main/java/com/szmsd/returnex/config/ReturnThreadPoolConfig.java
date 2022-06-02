package com.szmsd.returnex.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @ClassName: ReturnThreadPoolConfig
 * @Description:
 * @Author: 11
 * @Date: 2021-08-25 13:59
 */
@Slf4j
@Configuration
public class ReturnThreadPoolConfig {

    private final int availableProcessors = Runtime.getRuntime().availableProcessors();

    @Bean("returnThreadTaskPool")
    ThreadPoolTaskExecutor returnThreadTaskPool() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(Math.max(availableProcessors, 8));
        threadPoolTaskExecutor.setMaxPoolSize(Math.max(availableProcessors * 2, 12));
        threadPoolTaskExecutor.setQueueCapacity(1000);
        threadPoolTaskExecutor.setThreadNamePrefix("[Return]-");
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        threadPoolTaskExecutor.initialize();
        log.info("init returnThreadTaskPool {}", threadPoolTaskExecutor);
        return threadPoolTaskExecutor;
    }

}
