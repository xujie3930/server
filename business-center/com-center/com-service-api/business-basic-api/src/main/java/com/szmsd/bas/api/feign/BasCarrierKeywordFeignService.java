package com.szmsd.bas.api.feign;

import com.szmsd.bas.api.factory.BasCarrierKeywordFeignServiceFallbackFactory;
import com.szmsd.common.core.constant.ServiceNameConstants;
import com.szmsd.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "basCarrierKeywordFeignService", value = ServiceNameConstants.BUSINESS_BAS, fallbackFactory = BasCarrierKeywordFeignServiceFallbackFactory.class)
public interface BasCarrierKeywordFeignService {

    @GetMapping("/bas-carrier-keyword/checkExistKeyword")
    R<Boolean> checkExistKeyword(@RequestParam(value = "carrierCode") String carrierCode,
                                 @RequestParam(value = "text") String text);
}
