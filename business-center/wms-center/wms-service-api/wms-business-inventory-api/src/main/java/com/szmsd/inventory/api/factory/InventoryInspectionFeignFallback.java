package com.szmsd.inventory.api.factory;

import com.szmsd.common.core.domain.R;
import com.szmsd.inventory.api.feign.InventoryInspectionFeignService;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class InventoryInspectionFeignFallback implements FallbackFactory<InventoryInspectionFeignService> {

    @Override
    public InventoryInspectionFeignService create(Throwable throwable) {
        return dto -> R.convertResultJson(throwable);
    }

}
