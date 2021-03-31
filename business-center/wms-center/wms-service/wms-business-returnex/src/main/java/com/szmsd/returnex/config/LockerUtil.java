package com.szmsd.returnex.config;

import com.szmsd.common.core.exception.com.CommonException;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * copy from #{@link com.szmsd.inventory.service.LockerUtil}
 * @author zhangyuyuan
 * @date 2020-12-26 14:41
 */
public class LockerUtil<E> {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final RedissonClient redissonClient;

    public LockerUtil(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public void doWorker(String key, Worker worker) {
        doLocker(key, () -> {
            worker.execute();
            return null;
        });
    }

    /**
     * execute
     */
    public E doExecute(String key, Callback<E> callback) {
        return doLocker(key, callback);
    }

    protected E doLocker(String key, Callback<E> callback) {
        logger.info("================thread id: {}, do begin", Thread.currentThread().getId());
        // key
        RLock lock = redissonClient.getLock(key);
        // time 5 seconds
        long time = 5;
        TimeUnit timeUnit = TimeUnit.SECONDS;
        try {
            if (lock.tryLock(time, timeUnit)) {
                return callback.execute();
            } else {
                throw new CommonException("999", "执行失败，资源被占用");
            }
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
            throw new CommonException("999", "执行报错");
        } finally {
            lock.unlock();
        }
    }

    public void tryLock(String key, Worker worker) {
        logger.info("================thread id: {}, do begin", Thread.currentThread().getId());
        // key
        RLock lock = redissonClient.getLock(key);
        try {
            if (lock.tryLock()) {
                worker.execute();
            }
        } finally {
            lock.unlock();
        }
    }

    @FunctionalInterface
    public interface Worker {
        void execute();
    }

    /**
     * 业务逻辑回调接口。
     */
    @FunctionalInterface
    public interface Callback<E> {
        /**
         * execute
         *
         * @return E
         */
        E execute();
    }
}
