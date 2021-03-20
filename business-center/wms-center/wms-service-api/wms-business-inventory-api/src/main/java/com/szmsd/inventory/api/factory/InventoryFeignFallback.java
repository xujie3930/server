package com.szmsd.inventory.api.factory;

import com.szmsd.common.core.domain.R;
import com.szmsd.inventory.api.feign.InventoryFeignService;
import com.szmsd.inventory.domain.dto.InboundInventoryDTO;
import com.szmsd.inventory.domain.dto.InventorySkuVolumeQueryDTO;
import com.szmsd.inventory.domain.vo.InventorySkuVolumeVO;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InventoryFeignFallback implements FallbackFactory<InventoryFeignService> {
    @Override
    public InventoryFeignService create(Throwable throwable) {
        return new InventoryFeignService() {
            @Override
            public R inbound(InboundInventoryDTO receivingRequest) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<List<InventorySkuVolumeVO>> querySkuVolume(InventorySkuVolumeQueryDTO inventorySkuVolumeQueryDTO) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
