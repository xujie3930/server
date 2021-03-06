package com.szmsd.bas.api.factory;

import com.szmsd.bas.api.feign.BasTransactionFeignService;
import com.szmsd.bas.dto.BasTransactionDTO;
import com.szmsd.common.core.domain.R;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class BasTransactionFeignFallback implements FallbackFactory<BasTransactionFeignService> {

    @Override
    public BasTransactionFeignService create(Throwable cause) {
        return new BasTransactionFeignService() {
            @Override
            public R save(BasTransactionDTO basTransactionDTO) {
                return R.convertResultJson(cause);
            }

            @Override
            public R<Boolean> idempotent(BasTransactionDTO basTransactionDTO) {
                return R.convertResultJson(cause);
            }
        };
    }
}
