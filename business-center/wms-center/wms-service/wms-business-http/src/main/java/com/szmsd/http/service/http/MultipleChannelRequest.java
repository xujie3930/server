package com.szmsd.http.service.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangyuyuan
 * @date 2021-04-20 14:06
 */
public class MultipleChannelRequest {
    private final static Logger LOGGER = LoggerFactory.getLogger(MultipleChannelRequest.class);

    private final GlobalThreadPoolExecutor poolExecutor;

    private MultipleChannelRequest() {
        LOGGER.info("开始初始化线程池");
        // 初始化线程池
        poolExecutor = new GlobalThreadPoolExecutor(8, 24,
                1800, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(100),
                new GlobalThreadPoolExecutor.NamedThreadFactory("MultipleChannelRequest"),
                new ThreadPoolExecutor.CallerRunsPolicy());
        LOGGER.info("线程池初始化完成");
    }

    public static void run(NoneCallback noneCallback) {
        GlobalThreadPoolExecutor poolExecutor = getInstance().poolExecutor;
        poolExecutor.execute(() -> {
            LOGGER.info("开始执行任务");
            try {
                noneCallback.run();
            } catch (Exception e) {
                LOGGER.error("执行任务失败：" + e.getMessage(), e);
            }
            LOGGER.info("任务执行完成");
            poolExecutor.printPoolExecutorInfo(LOGGER);
        });
    }

    private static MultipleChannelRequest getInstance() {
        return MultipleChannelRequestInstance.INSTANCE;
    }

    private static class MultipleChannelRequestInstance {
        private static final MultipleChannelRequest INSTANCE = new MultipleChannelRequest();
    }
}
