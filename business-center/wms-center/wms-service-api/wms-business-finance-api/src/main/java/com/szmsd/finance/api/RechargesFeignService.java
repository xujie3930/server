package com.szmsd.finance.api;

import com.szmsd.finance.api.factory.RechargeFeignFallback;
import com.szmsd.finance.enums.BusinessFssInterface;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author liulei
 */
@FeignClient(contextId = "FeignClient.RechargesFeignService", name = BusinessFssInterface.SERVICE_NAME, fallbackFactory = RechargeFeignFallback.class)
public interface RechargesFeignService {

}
