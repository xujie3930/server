package com.szmsd.bas.api.feign;

import com.szmsd.bas.api.factory.BasCarrierKeywordFeignServiceFallbackFactory;
import com.szmsd.common.core.constant.ServiceNameConstants;
import com.szmsd.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(contextId = "basCarrierKeywordFeignService", value = ServiceNameConstants.BUSINESS_BAS, fallbackFactory = BasCarrierKeywordFeignServiceFallbackFactory.class)
public interface BasCarrierKeywordFeignService {

    @GetMapping("/bas-carrier-keyword/checkExistKeyword")
    R<Boolean> checkExistKeyword(@RequestParam(value = "carrierCode") String carrierCode,
                                 @RequestParam(value = "text") String text);


    @PostMapping("/bas-carrier-keyword/selectCarrierKeyword")
    R<Map> selectCarrierKeyword(@RequestBody Map map);
}
