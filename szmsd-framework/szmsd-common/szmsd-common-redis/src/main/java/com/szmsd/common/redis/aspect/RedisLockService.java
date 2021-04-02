package com.szmsd.common.redis.aspect;

import org.springframework.lang.NonNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangyuyuan
 * @date 2020-11-26 026 17:10
 */
public interface RedisLockService {

    /**
     * 尝试获取锁
     *
     * @param key 锁Key
     * @return boolean
     */
    boolean tryLock(String key);

    /**
     * 尝试获取锁
     *
     * @param key     锁Key
     * @param timeout 等待超时时间
     * @param unit    时间单位
     * @return boolean
     */
    boolean tryLock(String key, long timeout, TimeUnit unit);

    /**
     * 尝试获取锁
     *
     * @param key     锁Key
     * @param timeout 等待超时时间
     * @param expire  有效期
     * @param unit    时间单位
     * @return boolean
     */
    boolean tryLock(String key, long timeout, long expire, TimeUnit unit);

    /**
     * 尝试获取锁
     *
     * @param keys 锁Key
     * @return boolean
     */
    boolean tryLock(List<String> keys);

    /**
     * 尝试获取锁
     *
     * @param keys    锁Key
     * @param timeout 等待超时时间
     * @param unit    时间单位
     * @return boolean
     */
    boolean tryLock(List<String> keys, long timeout, TimeUnit unit);

    /**
     * 尝试获取锁
     *
     * @param keys    锁Key
     * @param timeout 等待超时时间
     * @param expire  有效期
     * @param unit    时间单位
     * @return boolean
     */
    boolean tryLock(List<String> keys, long timeout, long expire, TimeUnit unit);

    /**
     * 释放锁
     *
     * @param key 锁Key
     */
    void releaseLock(@NonNull String key);

    /**
     * 释放锁
     *
     * @param keys 锁Key
     */
    void releaseLock(@NonNull List<String> keys);
}
