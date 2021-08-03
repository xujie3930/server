package com.szmsd.finance.api.feign.factory;

import com.szmsd.common.core.domain.R;
import com.szmsd.finance.api.feign.AccountSerialBillFeignService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class AccountSerialBillFeignFallback implements FallbackFactory<AccountSerialBillFeignService> {

    @Override
    public AccountSerialBillFeignService create(Throwable throwable) {
        return dto -> R.convertResultJson(throwable);
    }

}
