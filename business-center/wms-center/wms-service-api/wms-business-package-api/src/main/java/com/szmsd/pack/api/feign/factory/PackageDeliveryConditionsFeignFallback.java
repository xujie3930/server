package com.szmsd.pack.api.feign.factory;

import com.szmsd.common.core.domain.R;
import com.szmsd.pack.api.feign.PackageDeliveryConditionsFeignService;
import com.szmsd.pack.domain.PackageDeliveryConditions;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class PackageDeliveryConditionsFeignFallback implements FallbackFactory<PackageDeliveryConditionsFeignService> {

    @Override
    public PackageDeliveryConditionsFeignService create(Throwable throwable) {
        return new PackageDeliveryConditionsFeignService() {
            @Override
            public R<PackageDeliveryConditions> info(PackageDeliveryConditions packageDeliveryConditions) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
