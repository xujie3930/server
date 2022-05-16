package com.szmsd.bas.api.feign;

import com.szmsd.bas.api.BusinessBasInterface;
import com.szmsd.bas.api.factory.BasSkuRuleMatchingFeignFallback;
import com.szmsd.bas.domain.BasDeliveryServiceMatching;
import com.szmsd.bas.domain.BasOtherRules;
import com.szmsd.bas.domain.BasSkuRuleMatching;
import com.szmsd.bas.dto.BasDeliveryServiceMatchingDto;
import com.szmsd.bas.dto.BasSkuRuleMatchingDto;
import com.szmsd.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "FeignClient.BasSkuRuleMatchingFeignService", name = BusinessBasInterface.SERVICE_NAME, fallbackFactory = BasSkuRuleMatchingFeignFallback.class)
public interface BasSkuRuleMatchingFeignService {

    @PostMapping(value = "/bas-sku-rule-matching/getList")
    R<List<BasSkuRuleMatching>> getList(@RequestBody BasSkuRuleMatchingDto dto);


    @GetMapping(value = "/bas-other-rules/getInfo/{sellerCode}")
    R<BasOtherRules> getInfo(@PathVariable("sellerCode") String sellerCode);


    @PostMapping(value = "/bas-delivery-service-matching/getList")
    R<List<BasDeliveryServiceMatching>> getList(@RequestBody BasDeliveryServiceMatchingDto dto);
}
