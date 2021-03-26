package com.szmsd.bas.api.factory;

import com.szmsd.bas.api.domain.BasCountry;
import com.szmsd.bas.api.feign.BasCountryFeignService;
import com.szmsd.common.core.domain.R;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author zhangyuyuan
 * @date 2021-03-24 19:26
 */
@Component
public class BasCountryFeignServiceFallbackFactory implements FallbackFactory<BasCountryFeignService> {

    @Override
    public BasCountryFeignService create(Throwable throwable) {
        return new BasCountryFeignService() {
            @Override
            public R<BasCountry> queryByCountryCode(String countryCode) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
