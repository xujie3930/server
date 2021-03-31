package com.szmsd.http.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.api.BusinessHttpInterface;
import com.szmsd.http.api.feign.fallback.HtpInventoryCheckFeignFallback;
import com.szmsd.http.dto.CountingRequest;
import com.szmsd.http.vo.ResponseVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "FeignClient.HtpInventoryCheckFeignService", name = BusinessHttpInterface.SERVICE_NAME, fallbackFactory = HtpInventoryCheckFeignFallback.class)
public interface HtpInventoryCheckFeignService {

    @PostMapping("/api/inventory/counting")
    R<ResponseVO> counting(@RequestBody CountingRequest countingRequest);

}
