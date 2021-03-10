package com.szmsd.bas.api.feign;

import com.szmsd.bas.api.BusinessBasInterface;
import com.szmsd.bas.api.factory.BaseProductFeignFallback;
import com.szmsd.bas.domain.BaseProduct;
import com.szmsd.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "FeignClient.BaseProductFeignService", name = BusinessBasInterface.SERVICE_NAME, fallbackFactory = BaseProductFeignFallback.class)
public interface BaseProductFeignService {
    @PostMapping(value = "/base-product/checkSkuValidToDelivery")
    R<Boolean> checkSkuValidToDelivery(@RequestBody BaseProduct baseProduct);
}
