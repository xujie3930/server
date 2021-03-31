package com.szmsd.inventory.api.factory;

import com.szmsd.common.core.domain.R;
import com.szmsd.inventory.api.feign.InventoryCheckFeignService;
import com.szmsd.inventory.domain.dto.AdjustRequestDto;
import com.szmsd.inventory.domain.dto.CountingRequestDto;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class InventoryCheckFeignFallback implements FallbackFactory<InventoryCheckFeignService> {

    @Override
    public InventoryCheckFeignService create(Throwable throwable) {
        return new InventoryCheckFeignService() {

            @Override
            public R adjust(AdjustRequestDto adjustRequestDto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R counting(CountingRequestDto countingRequestDto) {
                return R.convertResultJson(throwable);
            }
        };
    }

}
