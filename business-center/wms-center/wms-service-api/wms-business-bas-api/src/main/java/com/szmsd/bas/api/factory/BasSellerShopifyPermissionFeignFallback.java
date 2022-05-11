package com.szmsd.bas.api.factory;

import com.szmsd.bas.api.feign.BasSellerShopifyPermissionFeignService;
import com.szmsd.bas.domain.BasSellerShopifyPermission;
import com.szmsd.common.core.domain.R;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BasSellerShopifyPermissionFeignFallback implements FallbackFactory<BasSellerShopifyPermissionFeignService> {

    @Override
    public BasSellerShopifyPermissionFeignService create(Throwable throwable) {
        return new BasSellerShopifyPermissionFeignService(){
            @Override
            public R<List<BasSellerShopifyPermission>> list(BasSellerShopifyPermission basSellerShopifyPermission) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
