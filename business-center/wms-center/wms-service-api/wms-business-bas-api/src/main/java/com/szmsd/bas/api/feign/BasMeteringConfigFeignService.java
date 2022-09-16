package com.szmsd.bas.api.feign;

import com.szmsd.bas.api.BusinessBasInterface;
import com.szmsd.bas.api.factory.BasMeteringConfigFeignFallback;
import com.szmsd.bas.dto.BasMeteringConfigDto;
import com.szmsd.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "FeignClient.BasMeteringConfigFeignService", name = BusinessBasInterface.SERVICE_NAME, fallbackFactory = BasMeteringConfigFeignFallback.class)
public interface BasMeteringConfigFeignService {

    @PostMapping(value = "/bas/basMeteringConfigController/intercept")
    R intercept(@RequestBody BasMeteringConfigDto basMeteringConfigDto);
}
