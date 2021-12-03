package com.szmsd.chargerules.api.feign;

import com.szmsd.chargerules.api.SpecialOperationInterface;
import com.szmsd.chargerules.api.feign.factory.SpecialOperationFeignFallback;
import com.szmsd.common.core.domain.R;
import com.szmsd.delivery.vo.DelOutboundOperationVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "FeignClient.OperationFeignService", name = SpecialOperationInterface.SERVICE_NAME, fallbackFactory = SpecialOperationFeignFallback.class)
public interface OperationFeignService {

    @ApiOperation(value = "业务计费 - 出库冻结余额")
    @PostMapping(value = "/operation/delOutboundFreeze")
    R delOutboundFreeze(@RequestBody DelOutboundOperationVO delOutboundVO);

    @PostMapping(value = "/operation/delOutboundThaw")
    R delOutboundThaw(@RequestBody DelOutboundOperationVO delOutboundVO);

    @PostMapping(value = "/operation/delOutboundCharge")
    R delOutboundCharge(@RequestBody DelOutboundOperationVO delOutboundVO);

}
