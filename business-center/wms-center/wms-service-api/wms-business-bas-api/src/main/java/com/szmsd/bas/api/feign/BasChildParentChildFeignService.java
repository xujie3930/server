package com.szmsd.bas.api.feign;

import com.szmsd.bas.api.BusinessBasInterface;
import com.szmsd.bas.api.factory.BasChildParentChildFeignFallback;
import com.szmsd.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "FeignClient.BasChildParentChildFeignService", name = BusinessBasInterface.SERVICE_NAME, fallbackFactory = BasChildParentChildFeignFallback.class)
public interface BasChildParentChildFeignService {

    @PostMapping(value = "/bas-child-parent-child/getChildCodeList")
    R<List<String>>  getChildCodeList(@RequestBody String sellerCode);
}
