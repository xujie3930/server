package com.szmsd.bas.api.feign;

import com.szmsd.bas.api.domain.vo.BasRegionSelectListVO;
import com.szmsd.bas.api.factory.BasRegionFeignServiceFallbackFactory;
import com.szmsd.common.core.constant.ServiceNameConstants;
import com.szmsd.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author zhangyuyuan
 * @date 2021-03-24 19:23
 */
@FeignClient(contextId = "BasRegionFeignService", value = ServiceNameConstants.BUSINESS_BAS, fallbackFactory = BasRegionFeignServiceFallbackFactory.class)
public interface BasRegionFeignService {

    @RequestMapping("/bas-country/queryByCountryCode")
    R<BasRegionSelectListVO> queryByCountryCode(@RequestParam("addressCode") String addressCode);
}
