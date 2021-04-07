package com.szmsd.pack.api.feign.serve.factory;

import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.pack.api.feign.serve.IBasFeignService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @ClassName: BasFeignServiceFallback
 * @Description: bas
 * @Author: 11
 * @Date: 2021/4/6 14:36
 */
@Slf4j
@Component
public class BasFeignServiceFallback implements FallbackFactory<IBasFeignService> {
    @Override
    public IBasFeignService create(Throwable throwable) {
        return () -> {
            throw new BaseException("获取用户sellerCode异常!");
        };
    }
}
