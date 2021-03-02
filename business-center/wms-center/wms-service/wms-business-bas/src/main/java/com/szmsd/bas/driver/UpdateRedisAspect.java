package com.szmsd.bas.driver;

import com.szmsd.bas.service.BasCommonService;
import com.szmsd.common.core.enums.CodeToNameEnum;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;


@Aspect
@Component
@Slf4j
@Order(0)
public class UpdateRedisAspect {

    @Resource
    private BasCommonService service;

    @Pointcut("execution(public * com.szmsd.bas.controller.*.*(..)) && @annotation(com.szmsd.bas.driver.UpdateRedis)")
    public void pointCut() {
    }

    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "pointCut()" , returning = "jsonResult")
    public void doAfterReturning(JoinPoint joinPoint, Object jsonResult) {
        log.info("UpdateRedisAspect运行后");
        handler(joinPoint);
    }

    private void handler(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();

        if (method != null) {
            UpdateRedis annotation = method.getAnnotation(UpdateRedis.class);
            if (null != annotation) {
                CodeToNameEnum type = annotation.type();
                if (null != type) {
                    service.updateRedis(type);
                    log.info("更新redis，类型为：{}" , type.toString());
                }
            }

        }
    }
}
