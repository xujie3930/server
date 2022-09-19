package com.szmsd.bas.api.feign;


import com.szmsd.bas.api.factory.BasTranslateFeignServiceFallbackFactory;
import com.szmsd.common.core.constant.ServiceNameConstants;
import com.szmsd.common.core.domain.R;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "BasTranslateFeignService", value = ServiceNameConstants.BUSINESS_BAS, fallbackFactory = BasTranslateFeignServiceFallbackFactory.class)
public interface BasTranslateFeignService {

    @ApiOperation(value = "异常中文转换英文", notes = "异常中文转换英文")
    @GetMapping("/TranslateController/Translate")
    R<String> Translate(@RequestParam("query") String query);

}
