package com.szmsd.bas.aspect;

import com.szmsd.bas.dto.BasSellerDto;
import com.szmsd.bas.enums.EmailEnum;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 卖家注册邮箱验证码切面
 */
@Slf4j
@Aspect
@Component
public class EmailCodeValid {

    @Resource
    private RedisService redisService;

    private EmailEnum varCode = EmailEnum.VAR_CODE;

    @Pointcut("execution(public * com.szmsd.bas.controller.BasSellerController.register(..))")
    public void pointCut() {
    }

    @Before(value = "pointCut()")
    private void before(JoinPoint point) {
        BasSellerDto seller = (BasSellerDto) point.getArgs()[1];
        String email = seller.getInitEmail();
        String key = varCode.name().concat("-").concat(email);
        String cacheObject = redisService.getCacheObject(key);
        AssertUtil.isTrue(StringUtils.isNotEmpty(cacheObject), "邮箱验证码已过期，请重新获取");
        AssertUtil.isTrue(StringUtils.equals(cacheObject, seller.getEmailCaptcha()), "邮箱验证码错误");
        log.info("before ---> 进入切面验证邮箱验证码, email={}, cacheCode={}, input={}", email, cacheObject, seller.getEmailCaptcha());
    }

    @AfterReturning(pointcut = "pointCut()", returning = "retValue")
    private void after(JoinPoint point, R<Boolean> retValue) {
        if (retValue != null && retValue.getData() != null && retValue.getData()) {
            BasSellerDto seller = (BasSellerDto) point.getArgs()[1];
            String email = seller.getInitEmail();
            String key = varCode.name().concat("-").concat(email);
            redisService.deleteObject(key);
            log.info("after ---> 验证删除,  email={}");
        }
    }

}