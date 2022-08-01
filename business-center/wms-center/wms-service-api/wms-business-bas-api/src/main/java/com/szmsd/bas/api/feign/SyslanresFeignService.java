package com.szmsd.bas.api.feign;

import com.szmsd.bas.api.factory.SyslanresFeignFallback;
import com.szmsd.bas.domain.SysLanres;
import com.szmsd.common.core.constant.ServiceNameConstants;
import com.szmsd.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(contextId = "FeignClient.SyslanresFeignService", name = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = SyslanresFeignFallback.class)
public interface SyslanresFeignService {
    @GetMapping("/syslanres/lists")
    public R lists(SysLanres sysLanres);
}
