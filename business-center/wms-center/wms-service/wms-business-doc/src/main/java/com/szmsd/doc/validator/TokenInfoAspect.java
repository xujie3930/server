package com.szmsd.doc.validator;

import com.szmsd.common.core.enums.CodeToNameEnum;
import com.szmsd.doc.component.IRemoterApi;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * @ClassName: TokenInfo
 * @Description:
 * @Author: 11
 * @Date: 2021-09-15 18:03
 */
@Slf4j
@Aspect
// @Component
public class TokenInfoAspect {

    private final IRemoterApi remoterApi;

    public TokenInfoAspect(IRemoterApi remoterApi) {
        this.remoterApi = remoterApi;
    }

    @Pointcut("execution(public * com.szmsd.doc.api..*(..))")
    public void pointCut() {
        System.out.println("init");
    }
    @Before(value = "pointCut()")
    public void before(JoinPoint joinPoint){
//        joinPoint.getSignature()
        System.out.println("进入方法");
        try {
            //TODO 判断是否存在token
            remoterApi.getUserInfo();
        } catch (Exception e) {
//            e.printStackTrace();
        }

    }
    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @AfterReturning(pointcut = "pointCut()")
    public void doAfterReturning(JoinPoint joinPoint) {
        CurrentUserInfo.remove();
    }
    /**
     * 处理完请求后执行
     *
     * @param joinPoint 切点
     */
    @AfterThrowing(pointcut = "pointCut()")
    public void doAfterThrowing(JoinPoint joinPoint) {
        CurrentUserInfo.remove();
    }

}
