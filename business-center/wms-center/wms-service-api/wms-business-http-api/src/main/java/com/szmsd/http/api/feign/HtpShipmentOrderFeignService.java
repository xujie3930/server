package com.szmsd.http.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.api.BusinessHttpInterface;
import com.szmsd.http.api.feign.fallback.HtpShipmentOrderFeignFallback;
import com.szmsd.http.vo.CarrierService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(contextId = "FeignClient.HtpShipmentOrderFeignService", name = BusinessHttpInterface.SERVICE_NAME, fallbackFactory = HtpShipmentOrderFeignFallback.class)
public interface HtpShipmentOrderFeignService {

    @GetMapping("/api/carrierService/shipmentOrders/http/services")
    R<List<CarrierService>> services();
}
