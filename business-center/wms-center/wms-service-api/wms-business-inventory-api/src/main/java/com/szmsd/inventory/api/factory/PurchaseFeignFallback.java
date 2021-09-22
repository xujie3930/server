package com.szmsd.inventory.api.factory;

import com.szmsd.common.core.domain.R;
import com.szmsd.inventory.api.feign.InventoryFeignService;
import com.szmsd.inventory.api.feign.PurchaseFeignService;
import com.szmsd.inventory.domain.dto.TransportWarehousingAddDTO;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @ClassName: PurchaseFeignFallback
 * @Description: feign fallback
 * @Author: 11
 * @Date: 2021-04-27 11:50
 */
@Component
public class PurchaseFeignFallback implements FallbackFactory<PurchaseFeignService> {
    @Override
    public PurchaseFeignService create(Throwable throwable) {
        return new PurchaseFeignService() {
            @Override
            public R<Integer> cancelByWarehouseNo(String warehouseNo) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R transportWarehousingSubmit(TransportWarehousingAddDTO transportWarehousingAddDTO) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
