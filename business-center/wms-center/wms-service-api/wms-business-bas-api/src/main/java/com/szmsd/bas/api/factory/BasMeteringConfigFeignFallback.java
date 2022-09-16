package com.szmsd.bas.api.factory;

import com.szmsd.bas.api.feign.BasMeteringConfigFeignService;
import com.szmsd.bas.dto.BasMeteringConfigDto;
import com.szmsd.common.core.domain.R;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class BasMeteringConfigFeignFallback implements FallbackFactory<BasMeteringConfigFeignService> {


    @Override
    public BasMeteringConfigFeignService create(Throwable throwable) {
        return new BasMeteringConfigFeignService() {
            @Override
            public R intercept(BasMeteringConfigDto basMeteringConfigDto) {
                return R.convertResultJson(throwable);
            }

        };
    }
}
