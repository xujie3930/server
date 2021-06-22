package com.szmsd.http.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.api.BusinessHttpInterface;
import com.szmsd.http.api.feign.fallback.HtpInventoryFeignFallback;
import com.szmsd.http.vo.InventoryInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "FeignClient.HtpInventoryFeignService", name = BusinessHttpInterface.SERVICE_NAME, fallbackFactory = HtpInventoryFeignFallback.class)
public interface HtpInventoryFeignService {

    @GetMapping("/api/inventory/http/listing")
    R<List<InventoryInfo>> listing(@RequestParam("warehouseCode") String warehouseCode, @RequestParam("sku") String sku);

}
