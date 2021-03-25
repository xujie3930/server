package com.szmsd.bas.api.factory;

import com.szmsd.bas.api.feign.BasWarehouseFeignService;
import com.szmsd.bas.domain.BasWarehouse;
import com.szmsd.bas.dto.AddWarehouseRequest;
import com.szmsd.common.core.domain.R;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BasWarehouseFeignFallback implements FallbackFactory<BasWarehouseFeignService> {

    @Override
    public BasWarehouseFeignService create(Throwable throwable) {
        return new BasWarehouseFeignService() {
            @Override
            public R saveOrUpdate(AddWarehouseRequest addWarehouseRequest) {
                log.info("创建/更新仓库失败: {}", throwable.getMessage());
                return R.convertResultJson(throwable);
            }

            @Override
            public R<BasWarehouse> queryByWarehouseCode(String warehouseCode) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
