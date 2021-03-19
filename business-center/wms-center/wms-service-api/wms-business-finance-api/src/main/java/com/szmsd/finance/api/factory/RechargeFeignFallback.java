package com.szmsd.finance.api.factory;

import com.szmsd.finance.api.RechargesFeignService;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author liulei
 */
@Component
public class RechargeFeignFallback implements FallbackFactory<RechargesFeignService> {
    @Override
    public RechargesFeignService create(Throwable cause) {
        return new RechargesFeignService(){

        };
    }
}
