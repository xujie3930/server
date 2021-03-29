package com.szmsd.chargerules.api.feign;

import com.szmsd.chargerules.api.SpecialOperationInterface;
import com.szmsd.chargerules.api.feign.factory.SpecialOperationFeignFallback;
import com.szmsd.chargerules.dto.BasSpecialOperationRequestDTO;
import com.szmsd.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "FeignClient.BaseSpecialOperationFeignService", name = SpecialOperationInterface.SERVICE_NAME, fallbackFactory = SpecialOperationFeignFallback.class)
public interface SpecialOperationFeignService {

    @PostMapping(value = "/api/base/specialOperation")
    R<Boolean> specialOperation(@RequestBody BasSpecialOperationRequestDTO baseProduct);

}
