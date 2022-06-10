package com.szmsd.bas.api.factory;

import com.szmsd.bas.api.feign.BasPartnerFeignService;
import com.szmsd.bas.domain.BasPartner;
import com.szmsd.common.core.domain.R;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class BasPartnerFeignFallback implements FallbackFactory<BasPartnerFeignService> {

    @Override
    public BasPartnerFeignService create(Throwable throwable) {
        return new BasPartnerFeignService() {
            @Override
            public R<BasPartner> getByCode(BasPartner entity) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
