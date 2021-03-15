package com.szmsd.bas.api.factory;

import com.szmsd.bas.api.feign.BaseProductFeignService;
import com.szmsd.bas.domain.BaseProduct;
import com.szmsd.common.core.domain.R;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@Component
public class BaseProductFeignFallback implements FallbackFactory<BaseProductFeignService> {

    @Override
    public BaseProductFeignService create(Throwable throwable) {
        return new BaseProductFeignService() {
            @Override
            public R<Boolean> checkSkuValidToDelivery(@RequestBody BaseProduct baseProduct){
                return R.convertResultJson(throwable);
            }

            @Override
            public R<List<BaseProduct>> listSKU(@RequestBody BaseProduct baseProduct){
                return R.convertResultJson(throwable);
            }
        };
    }
}
