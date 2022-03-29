package com.szmsd.pack.api.feign;

import com.szmsd.common.core.domain.R;
import com.szmsd.pack.api.BusinessPackageInterface;
import com.szmsd.pack.api.feign.factory.PackageDeliveryConditionsFeignFallback;
import com.szmsd.pack.domain.PackageDeliveryConditions;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "FeignClient.PackageDeliveryConditionsFeignService", name = BusinessPackageInterface.SERVICE_NAME, fallbackFactory = PackageDeliveryConditionsFeignFallback.class)
public interface PackageDeliveryConditionsFeignService {

    @PostMapping(value = "/package-delivery-conditions/info")
    R<PackageDeliveryConditions> info(@RequestBody PackageDeliveryConditions packageDeliveryConditions);
}
