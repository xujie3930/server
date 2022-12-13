package com.szmsd.finance.api.feign.factory;

import com.szmsd.common.core.domain.R;
import com.szmsd.finance.api.feign.ConvertUnitFeignService;
import com.szmsd.finance.domain.FssConvertUnit;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ConvertUnitFeignFallback implements FallbackFactory<ConvertUnitFeignService> {
    @Override
    public ConvertUnitFeignService create(Throwable throwable) {

        return new ConvertUnitFeignService(){

            @Override
            public R<List<FssConvertUnit>> findAll() {
                return R.convertResultJson(throwable);
            }
        };
    }
}
