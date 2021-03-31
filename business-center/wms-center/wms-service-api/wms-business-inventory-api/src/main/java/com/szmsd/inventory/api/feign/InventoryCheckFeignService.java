package com.szmsd.inventory.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.inventory.api.BusinessInventoryInterface;
import com.szmsd.inventory.api.factory.InventoryFeignFallback;
import com.szmsd.inventory.domain.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "FeignClient.InventoryCheckFeignService", name = BusinessInventoryInterface.SERVICE_NAME, fallbackFactory = InventoryFeignFallback.class)
public interface InventoryCheckFeignService {

    @PostMapping("/api/inventory/adjust")
    R<Integer> adjust(@RequestBody AdjustRequestDto adjustRequestDto);

    @PostMapping("/api/inventory/counting")
    R<Integer> counting(@RequestBody CountingRequestDto countingRequestDto);

}
