package com.szmsd.ec.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.ec.dto.TransferCallbackDTO;
import com.szmsd.ec.factory.CommonOrderFeignFallback;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@FeignClient(contextId = "FeignClient.CommonOrderFeignService", name = "wms-business-ec", fallbackFactory = CommonOrderFeignFallback.class)
public interface CommonOrderFeignService {

    @ApiOperation("转仓库单回调")
    @PostMapping("transferCallback")
    R transferCallback(@RequestBody @Valid TransferCallbackDTO callbackDTO);
}
