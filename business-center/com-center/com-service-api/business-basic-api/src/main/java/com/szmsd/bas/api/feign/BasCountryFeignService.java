package com.szmsd.bas.api.feign;

import com.szmsd.bas.api.domain.BasCountry;
import com.szmsd.bas.api.factory.BasCountryFeignServiceFallbackFactory;
import com.szmsd.common.core.constant.ServiceNameConstants;
import com.szmsd.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author zhangyuyuan
 * @date 2021-03-24 19:23
 */
@FeignClient(contextId = "BasCountryFeignService", value = ServiceNameConstants.BUSINESS_BAS, fallbackFactory = BasCountryFeignServiceFallbackFactory.class)
public interface BasCountryFeignService {

    @GetMapping("/bas-country/queryByCountryCode")
    R<BasCountry> queryByCountryCode(String countryCode);
}
