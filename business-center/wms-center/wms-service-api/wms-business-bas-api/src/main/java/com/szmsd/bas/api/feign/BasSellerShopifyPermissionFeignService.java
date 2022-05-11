package com.szmsd.bas.api.feign;

import com.szmsd.bas.api.BusinessBasInterface;
import com.szmsd.bas.api.factory.BasSellerShopifyPermissionFeignFallback;
import com.szmsd.bas.domain.BasSellerShopifyPermission;
import com.szmsd.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "FeignClient.BasSellerShopifyPermissionFeignService", name = BusinessBasInterface.SERVICE_NAME, fallbackFactory = BasSellerShopifyPermissionFeignFallback.class)
public interface BasSellerShopifyPermissionFeignService {

    @PostMapping(value = "/bas-seller-shopify-permission/list")
    R<List<BasSellerShopifyPermission>> list(@RequestBody BasSellerShopifyPermission basSellerShopifyPermission);
}
