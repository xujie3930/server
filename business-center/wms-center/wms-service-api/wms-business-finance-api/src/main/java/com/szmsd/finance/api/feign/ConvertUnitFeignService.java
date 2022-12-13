package com.szmsd.finance.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.finance.api.feign.factory.ConvertUnitFeignFallback;
import com.szmsd.finance.domain.FssConvertUnit;
import com.szmsd.finance.enums.BusinessFssInterface;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(contextId = "FeignClient.ConvertUnitFeignService", name = BusinessFssInterface.SERVICE_NAME, fallbackFactory = ConvertUnitFeignFallback.class)
public interface ConvertUnitFeignService {

    @ApiOperation(value = "查询用户信用额信息")
    @GetMapping("/convert-unit/find-all")
    R<List<FssConvertUnit>> findAll();
}
