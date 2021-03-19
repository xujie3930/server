package com.szmsd.bas.api.factory;

import com.szmsd.bas.api.feign.SysDictDataFeignService;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author liulei
 */
@Component
public class SysDictDataServiceFallbackFactory implements FallbackFactory<SysDictDataFeignService> {
    private static final Logger log = LoggerFactory.getLogger(SysDictDataServiceFallbackFactory.class);
    @Override
    public SysDictDataFeignService create(Throwable throwable) {
        log.error("用户服务调用失败:{}", throwable.getMessage());
        return new SysDictDataFeignService(){

        };
    }
}
