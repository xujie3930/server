package com.szmsd.common.redis.aspect;

import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangyuyuan
 * @date 2020-11-26 026 17:10
 */
@Service
public class RedisLockServiceImpl implements RedisLockService {
    /**
     * 单个业务持有锁的时间60s，防止死锁
     */
    private final static long LOCK_EXPIRE = 60 * 1000L;
    /**
     * 默认尝试3s
     */
    private final static long LOCK_TRY_TIMEOUT = 3 * 1000L;
    private final Logger log = LoggerFactory.getLogger(RedisLockServiceImpl.class);
    @Autowired
    private RedissonClient redissonClient;

    @Override
    public boolean tryLock(String key) {
        return tryLock(key, LOCK_TRY_TIMEOUT, LOCK_EXPIRE, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean tryLock(String key, long timeout, TimeUnit unit) {
        return tryLock(key, timeout, LOCK_EXPIRE, unit);
    }

    @Override
    public boolean tryLock(String key, long timeout, long expire, TimeUnit unit) {
        boolean getLock = false;
        RLock lock = redissonClient.getLock(key);
        try {
            getLock = lock.tryLock(timeout, expire, unit);
        } catch (InterruptedException e) {
            log.error("获取锁失败", e);
        }
        return getLock;
    }

    @Override
    public boolean tryLock(List<String> keys) {
        return tryLock(keys, LOCK_TRY_TIMEOUT, LOCK_EXPIRE, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean tryLock(List<String> keys, long timeout, TimeUnit unit) {
        return tryLock(keys, timeout, LOCK_EXPIRE, unit);
    }

    @Override
    public boolean tryLock(List<String> keys, long timeout, long expire, TimeUnit unit) {
        // 所有的key都锁定了，就成功，失败一个全部失败
        if (CollectionUtils.isEmpty(keys)) {
            return false;
        }
        boolean getLock = false;
        for (String key : keys) {
            getLock = tryLock(key, timeout, expire, unit);
            if (!getLock) {
                // 失败，返回
                break;
            }
        }
        return getLock;
    }

    @Override
    public void releaseLock(String key) {
        RLock lock = redissonClient.getLock(key);
        if (null != lock && lock.isLocked()) {
            lock.unlock();
        }
    }

    @Override
    public void releaseLock(List<String> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return;
        }
        for (String key : keys) {
            releaseLock(key);
        }
    }
}
