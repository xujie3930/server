package com.szmsd.bas.api.factory;

import com.szmsd.bas.api.feign.BasChildParentChildFeignService;
import com.szmsd.bas.api.feign.BasPartnerFeignService;
import com.szmsd.bas.api.feign.BaseProductFeignService;
import com.szmsd.bas.domain.BasPartner;
import com.szmsd.common.core.domain.R;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BasChildParentChildFeignFallback implements FallbackFactory<BasChildParentChildFeignService> {


    @Override
    public BasChildParentChildFeignService create(Throwable throwable) {
        return new BasChildParentChildFeignService() {
            @Override
            public R<List<String>> getChildCodeList(String sellerCode) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
