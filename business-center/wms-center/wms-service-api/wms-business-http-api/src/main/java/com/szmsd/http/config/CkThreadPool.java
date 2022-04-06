package com.szmsd.http.config;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;

/**
 * @ClassName: CkThreadPool
 * @Description: CK1推送线程池
 * 因为不涉及计算等，只是掉接口所有核心数适当多开
 * @Author: 11
 * @Date: 2021-12-15 13:42
 */
@Slf4j
public class CkThreadPool extends ThreadPoolTaskExecutor {

    /**
     * cpu数
     */
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    public CkThreadPool() {
        setThreadNamePrefix("【CK1-Push】--");
        setQueueCapacity(100);
        setCorePoolSize(Math.max(CPU_COUNT * 2, 10));
        setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        setMaxPoolSize(Math.max(CPU_COUNT * 3, 10));
        setKeepAliveSeconds((int) TimeUnit.MICROSECONDS.toSeconds(10));
        initialize();
        log.info("【ckTreadPool init】 finish {}", JSONObject.toJSONString(this));
    }

    @Override
    public void execute(Runnable task) {
        super.execute(task);
    }

    @Override
    public void execute(Runnable task, long startTimeout) {
        super.execute(task, startTimeout);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(task);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return super.submit(task);
    }
}
