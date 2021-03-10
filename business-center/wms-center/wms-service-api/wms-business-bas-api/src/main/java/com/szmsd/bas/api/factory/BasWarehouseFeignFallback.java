package com.szmsd.bas.api.factory;

import com.szmsd.bas.api.feign.BasWarehouseFeignService;
import com.szmsd.common.core.domain.R;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BasWarehouseFeignFallback implements FallbackFactory<BasWarehouseFeignService> {

    @Override
    public BasWarehouseFeignService create(Throwable throwable) {
        return addWarehouseRequest -> {
            log.info("创建/更新仓库失败: {}", throwable.getMessage());
            return R.convertResultJson(throwable);
        };
    }
}
