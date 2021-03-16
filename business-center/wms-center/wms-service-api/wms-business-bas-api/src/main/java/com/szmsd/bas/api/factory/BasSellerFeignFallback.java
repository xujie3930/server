package com.szmsd.bas.api.factory;

import com.szmsd.bas.api.feign.BasSellerFeignService;
import com.szmsd.bas.domain.BasSeller;
import com.szmsd.common.core.domain.R;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

@Component
public class BasSellerFeignFallback implements FallbackFactory<BasSellerFeignService> {
    @Override
    public BasSellerFeignService create(Throwable throwable) {
        return new BasSellerFeignService() {
            @Override
            public R<String> getSellerCode(@RequestBody BasSeller basSeller){
                return R.convertResultJson(throwable);
            }
        };
    }
}
