package com.szmsd.bas.api.feign;

import com.szmsd.bas.api.BusinessBasInterface;
import com.szmsd.bas.api.factory.BasPartnerFeignFallback;
import com.szmsd.bas.domain.BasPartner;
import com.szmsd.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "FeignClient.BasPartnerFeignService", name = BusinessBasInterface.SERVICE_NAME, fallbackFactory = BasPartnerFeignFallback.class)
public interface BasPartnerFeignService {

    @PostMapping(value = "/bas/partner/getByCode")
    R<BasPartner> getByCode(@RequestBody BasPartner entity);
}
