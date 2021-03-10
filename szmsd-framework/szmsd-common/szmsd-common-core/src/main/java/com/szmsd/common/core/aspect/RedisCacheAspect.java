package com.szmsd.common.core.aspect;

import com.szmsd.common.core.annotation.RedisCache;
import com.szmsd.common.core.enums.RedisLanguageFieldEnum;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
public class RedisCacheAspect {

    @Resource
    public RedisTemplate redisTemplate;

    @Pointcut("@annotation(com.szmsd.common.core.annotation.RedisCache)")
    public void pointCut() {

    }

    @Before(value = "pointCut()")
    private void before(JoinPoint point) {
    }

    @AfterReturning(pointcut = "pointCut()", returning = "retValue")
    private void after(JoinPoint join, Object retValue) {
        Signature signature = join.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method targetMethod = methodSignature.getMethod();
        RedisCache declaredAnnotation = targetMethod.getDeclaredAnnotation(RedisCache.class);
        Object[] args = join.getArgs();
        int paramIndex = declaredAnnotation.paramIndex();
        if (args.length < paramIndex) {
            log.error("Method[{}] Param number {} < {} ", targetMethod.getName(), args.length, paramIndex);
            return;
        }
        // 处理redis
        RedisLanguageFieldEnum redisLanguageFieldEnum = declaredAnnotation.redisLanguageFieldEnum();
        Map<String, Object> sourceMap = BeanMapperUtil.map(args[paramIndex], HashMap.class);
        handle(redisLanguageFieldEnum, sourceMap);
    }

    /**
     * 更新redis
     *
     * @param redisLanguageFieldEnum
     * @param sourceMap
     */
    private void handle(RedisLanguageFieldEnum redisLanguageFieldEnum, Map<String, Object> sourceMap) {
        String table = redisLanguageFieldEnum.getTable();
        String key = redisLanguageFieldEnum.getKey();
        String languageName = redisLanguageFieldEnum.getLanguageName();
        Map<String, Object> redisMap = new HashMap<>();
        Arrays.asList(languageName.split(",")).forEach(item -> {
            String[] split = item.split("-");
            String field = split[0].trim();
            String language = split[1].trim();
            Object value = sourceMap.get(field);
            redisMap.put(language, value);
        });
        redisTemplate.opsForHash().put(table, sourceMap.get(key), redisMap);
    }
}
