package com.szmsd.bas.api.feign;

import com.szmsd.bas.api.BusinessBasInterface;
import com.szmsd.bas.api.factory.BasWarehouseFeignFallback;
import com.szmsd.bas.dto.AddWarehouseRequest;
import com.szmsd.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "FeignClient.BasWarehouseFeignService", name = BusinessBasInterface.SERVICE_NAME, fallbackFactory = BasWarehouseFeignFallback.class)
public interface BasWarehouseFeignService {

    /**
     * 创建/更新仓库
     * @param addWarehouseRequest
     * @return
     */
    @PostMapping("/bas/warehouse/saveOrUpdate")
    R saveOrUpdate(@RequestBody AddWarehouseRequest addWarehouseRequest);
}
