package com.szmsd.bas.api.factory;

import com.szmsd.bas.api.feign.BasCarrierKeywordFeignService;
import com.szmsd.common.core.domain.R;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BasCarrierKeywordFeignServiceFallbackFactory implements FallbackFactory<BasCarrierKeywordFeignService> {
    @Override
    public BasCarrierKeywordFeignService create(Throwable throwable) {
        return new BasCarrierKeywordFeignService() {
            @Override
            public R<Boolean> checkExistKeyword(String carrierCode, String text) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<Map> selectCarrierKeyword(Map map) {
                return null;
            }
        };
    }
}
