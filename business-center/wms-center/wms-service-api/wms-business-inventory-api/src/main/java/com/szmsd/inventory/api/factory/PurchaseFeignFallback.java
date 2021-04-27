package com.szmsd.inventory.api.factory;

import com.szmsd.common.core.domain.R;
import com.szmsd.inventory.api.feign.InventoryFeignService;
import com.szmsd.inventory.api.feign.PurchaseFeignService;
import feign.hystrix.FallbackFactory;

/**
 * @ClassName: PurchaseFeignFallback
 * @Description: feign fallback
 * @Author: 11
 * @Date: 2021-04-27 11:50
 */
public class PurchaseFeignFallback implements FallbackFactory<PurchaseFeignService> {
    @Override
    public PurchaseFeignService create(Throwable throwable) {
        return new PurchaseFeignService() {
            @Override
            public R<Integer> cancelByWarehouseNo(String warehouseNo) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
