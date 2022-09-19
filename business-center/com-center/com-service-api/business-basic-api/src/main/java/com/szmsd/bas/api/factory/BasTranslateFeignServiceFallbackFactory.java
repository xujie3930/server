package com.szmsd.bas.api.factory;

import com.szmsd.bas.api.feign.BasTranslateFeignService;
import com.szmsd.common.core.domain.R;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class BasTranslateFeignServiceFallbackFactory  implements FallbackFactory<BasTranslateFeignService> {
    private static final Logger log = LoggerFactory.getLogger(BasTranslateFeignServiceFallbackFactory.class);


    @Override
    public BasTranslateFeignService create(Throwable throwable) {
        return new BasTranslateFeignService(){

            @Override
            public R<String> Translate(String query) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
