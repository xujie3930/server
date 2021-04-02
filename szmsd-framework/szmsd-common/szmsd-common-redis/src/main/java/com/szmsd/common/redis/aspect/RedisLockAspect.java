package com.szmsd.common.redis.aspect;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.CollectionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author zhangyuyuan
 * @date 2020-11-26 026 17:12
 */
@Aspect
@Component
public class RedisLockAspect {

    private final Logger log = LoggerFactory.getLogger(RedisLockAspect.class);

    @Autowired
    private RedisLockService redisLockService;

    @Pointcut("@annotation(com.szmsd.common.redis.aspect.RedisLock)")
    public void redisLock() {
    }

    @Around("redisLock()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        List<String> redisLockKeys = this.getRedisKey(joinPoint);
        log.info("redisLockKeys:{}", redisLockKeys);
        boolean lock = CollectionUtils.isNotEmpty(redisLockKeys);
        if (lock) {
            boolean isLock = this.redisLockService.tryLock(redisLockKeys);
            if (!isLock) {
                throw new RedisLockException("此单据，其他用户正在处理中，请1分钟后刷新再试");
            }
        }
        try {
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            log.error(throwable.getMessage(), throwable);
            throw throwable;
        } finally {
            if (lock) {
                this.redisLockService.releaseLock(redisLockKeys);
            }
        }
    }

    private List<String> getRedisKey(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        RedisLock annotation = signature.getMethod().getAnnotation(RedisLock.class);
        String contextId = annotation.contextId();
        String prefix;
        if ("".equals(contextId)) {
            prefix = joinPoint.getTarget().getClass().getSimpleName().toUpperCase().concat("-").concat(signature.getMethod().getName().toUpperCase());
        } else {
            prefix = contextId;
        }
        Map<Object, Object> map = new HashMap<>();
        for (int i = 0; i < signature.getParameterNames().length; i++) {
            map.put(signature.getParameterNames()[i], joinPoint.getArgs()[i]);
        }
        String code = "";
        List<?> codes = null;
        String parameter = annotation.value();
        if (StringUtils.isEmpty(parameter)) {
            throw new RedisLockException("key定义不存在");
        }
        Object parameterObject = this.getParameterObject(map, annotation);
        if (parameterObject == null) {
            throw new RedisLockException("参数定义不存在");
        }
        if (parameter.contains(".")) {
            String ap = parameter.substring(parameter.indexOf(".") + 1);
            // 如果是列表
            if (parameterObject instanceof List) {
                parameterObject = ((List<?>) parameterObject).get(0);
            }
            Object value = ReflectUtil.getValue(parameterObject, ap);
            if (value != null) {
                if (value instanceof List) {
                    codes = (List<?>) value;
                } else {
                    code = value.toString();
                }
            }
        } else {
            code = JSONObject.toJSONString(parameterObject).replace("\"", "");
        }
        if (StringUtils.isEmpty(code) && CollectionUtils.isEmpty(codes)) {
            if (annotation.IgnoreNull()) {
                log.debug("出现空值，已忽略，serviceName：{}，value：{}", annotation.serviceName(), annotation.value());
                return Collections.emptyList();
            } else {
                throw new RedisLockException("获取定义的key失败");
            }
        }
        // 前缀key
        String prefixKey = annotation.serviceName().concat(":").concat(prefix).concat(":");
        List<String> keys = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(codes)) {
            for (Object c : codes) {
                keys.add(prefixKey.concat(c.toString()));
            }
        } else {
            keys.add(prefixKey.concat(code));
        }
        return keys;
    }

    private Object getParameterObject(Map<Object, Object> map, RedisLock annotation) {
        String parameter = annotation.value();
        String bp = "";
        if (parameter.contains(".")) {
            bp = parameter.substring(0, parameter.indexOf("."));
            return map.get(bp);
        }
        return map.get(parameter);
    }
}
