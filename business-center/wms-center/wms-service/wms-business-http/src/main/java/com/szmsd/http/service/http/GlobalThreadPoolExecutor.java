package com.szmsd.http.service.http;

import org.slf4j.Logger;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhangyuyuan
 * @date 2020-12-26 11:15
 */
public class GlobalThreadPoolExecutor extends ThreadPoolExecutor {

    public GlobalThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public GlobalThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public GlobalThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public GlobalThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    public static GlobalThreadPoolExecutor defaultExecutor(String threadNamePrefix) {
        return defaultExecutor(16, 50, 180, TimeUnit.SECONDS, threadNamePrefix);
    }

    public static GlobalThreadPoolExecutor defaultExecutor(int corePoolSize, String threadNamePrefix) {
        return defaultExecutor(corePoolSize, corePoolSize, 180, TimeUnit.SECONDS, threadNamePrefix);
    }

    public static GlobalThreadPoolExecutor defaultExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, String threadNamePrefix) {
        return new GlobalThreadPoolExecutor(corePoolSize, maximumPoolSize,
                keepAliveTime, unit,
                new LinkedBlockingQueue<>(10000),
                new NamedThreadFactory(threadNamePrefix),
                new CallerRunsPolicy());
    }

    public void printPoolExecutorInfo(Logger logger) {
        BlockingQueue<Runnable> queue = this.getQueue();
        int queueSize = 0;
        if (null != queue) {
            queueSize = queue.size();
        }
        logger.info("CorePoolSize: {}, PoolSize: {}, ActiveCount: {}, QueueSize: {}", this.getCorePoolSize(), this.getPoolSize(), this.getActiveCount(), queueSize);
    }

    static class NamedThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        NamedThreadFactory(String threadNamePrefix) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = threadNamePrefix + "-" +
                    poolNumber.getAndIncrement() +
                    "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }

}
