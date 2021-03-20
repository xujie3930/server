package com.szmsd.finance.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.finance.api.feign.factory.RechargeFeignFallback;
import com.szmsd.finance.dto.CustPayDTO;
import com.szmsd.finance.dto.RechargesCallbackRequestDTO;
import com.szmsd.finance.enums.BusinessFssInterface;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author liulei
 */
//@FeignClient(contextId = "FeignClient.RechargesFeignService", name = BusinessFssInterface.SERVICE_NAME, fallbackFactory = RechargeFeignFallback.class)
@FeignClient(contextId = "FeignClient.RechargesFeignService", name = BusinessFssInterface.SERVICE_NAME)
public interface RechargesFeignService {

    @PostMapping("/accountBalance/rechargeCallback")
    R rechargeCallback(@RequestBody RechargesCallbackRequestDTO requestDTO);

    @PostMapping("/accountBalance/warehouseFeeDeduct")
    R warehouseFeeDeductions(@RequestBody CustPayDTO dto);
}
