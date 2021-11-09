package com.szmsd.delivery.config;

import cn.hutool.core.thread.NamedThreadFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolExecutorConfiguration {

    /**
     * 提审操作后台执行 - 线程池
     */
    public static final String THREADPOOLEXECUTOR_DELOUTBOUND_REVIEWED = "ThreadPoolExecutor-DelOutbound-Reviewed";

    /**
     * #D2 接收出库包裹信息
     * ShipmentPackingEvent
     */
    public static final String THREADPOOLEXECUTOR_SHIPMENTPACKINGEVENT = "ThreadPoolExecutor-ShipmentPackingEvent";

    /**
     * 重新获取标签文件
     */
    public static final String THREADPOOLEXECUTOR_SHIPMENTENUMLABEL = "ThreadPoolExecutor-ShipmentEnumLabel";

    @Bean(THREADPOOLEXECUTOR_DELOUTBOUND_REVIEWED)
    public ThreadPoolExecutor threadPoolExecutorDelOutboundReviewed() {
        // 获取机器核数
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        // 核心线程数量
        availableProcessors = availableProcessors * 2;
        // 队列
        LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(128);
        // 核心和最大一致
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(availableProcessors, availableProcessors, 30, TimeUnit.SECONDS, queue);
        // 线程池工厂
        NamedThreadFactory threadFactory = new NamedThreadFactory("DelOutbound-Reviewed", false);
        threadPoolExecutor.setThreadFactory(threadFactory);
        // 拒绝策略由主线程执行
        threadPoolExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return threadPoolExecutor;
    }

    @Bean(THREADPOOLEXECUTOR_SHIPMENTPACKINGEVENT)
    public ThreadPoolExecutor threadPoolExecutorShipmentPackingEvent() {
        // 获取机器核数
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        // 核心线程数量
        availableProcessors = availableProcessors * 2;
        // 队列
        LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(128);
        // 核心和最大一致
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(availableProcessors, availableProcessors, 30, TimeUnit.SECONDS, queue);
        // 线程池工厂
        NamedThreadFactory threadFactory = new NamedThreadFactory("ShipmentPackingEvent", false);
        threadPoolExecutor.setThreadFactory(threadFactory);
        // 拒绝策略由主线程执行
        threadPoolExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return threadPoolExecutor;
    }

    @Bean(THREADPOOLEXECUTOR_SHIPMENTENUMLABEL)
    public ThreadPoolExecutor threadPoolExecutorShipmentEnumLabel() {
        // 获取机器核数
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        // 核心线程数量
        availableProcessors = availableProcessors * 4;
        // 队列
        LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(256);
        // 核心和最大一致
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(availableProcessors, availableProcessors, 30, TimeUnit.SECONDS, queue);
        // 线程池工厂
        NamedThreadFactory threadFactory = new NamedThreadFactory("ShipmentEnumLabel", false);
        threadPoolExecutor.setThreadFactory(threadFactory);
        // 丢弃任务并抛出RejectedExecutionException异常
        threadPoolExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        return threadPoolExecutor;
    }
}
