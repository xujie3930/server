package com.szmsd.bas.api.factory;

import com.szmsd.bas.api.feign.BasSubFeignService;
import com.szmsd.common.core.domain.R;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author zhangyuyuan
 * @date 2021-03-26 10:45
 */
@Component
public class BasSubFeignServiceFallbackFactory implements FallbackFactory<BasSubFeignService> {

    @Override
    public BasSubFeignService create(Throwable throwable) {
        return new BasSubFeignService() {

            @Override
            public R<?> list(String code, String name) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
