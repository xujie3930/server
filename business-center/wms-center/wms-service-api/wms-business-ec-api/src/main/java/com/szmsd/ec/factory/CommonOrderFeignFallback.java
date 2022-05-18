package com.szmsd.ec.factory;

import com.szmsd.common.core.domain.R;
import com.szmsd.ec.dto.CallbackShippingResultDTO;
import com.szmsd.ec.dto.TransferCallbackDTO;
import com.szmsd.ec.feign.CommonOrderFeignService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.validation.Valid;

@Slf4j
@Component
public class CommonOrderFeignFallback implements FallbackFactory<CommonOrderFeignService> {

    @Override
    public CommonOrderFeignService create(Throwable throwable) {
        return new CommonOrderFeignService() {

            @Override
            public R transferCallback(@Valid TransferCallbackDTO callbackDTO) {
                return R.convertResultJson(throwable);
            }

        };
    }
}
