package com.szmsd.inventory.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.inventory.api.BusinessInventoryInterface;
import com.szmsd.inventory.api.factory.InventoryInspectionFeignFallback;
import com.szmsd.inventory.domain.dto.InboundInventoryInspectionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "FeignClient.InventoryInspectionFeignService", name = BusinessInventoryInterface.SERVICE_NAME, fallbackFactory = InventoryInspectionFeignFallback.class)
public interface InventoryInspectionFeignService {

    @PostMapping("/inventory/inspection/inboundInventory")
    R<Boolean> inbound(@RequestBody InboundInventoryInspectionDTO dto);

}
