package com.szmsd.bas.api.factory;

import com.szmsd.bas.api.feign.SyslanresFeignService;
import com.szmsd.bas.domain.SysLanres;
import com.szmsd.common.core.domain.R;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class SyslanresFeignFallback implements FallbackFactory<SyslanresFeignService> {
    @Override
    public SyslanresFeignService create(Throwable throwable) {
        return new SyslanresFeignService() {
            @Override
            public R lists(SysLanres sysLanres) {
                return null;
            }
        };
    }
}
