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
        log.info("create失败，服务调用降级{}", throwable);
        return dto -> R.convertResultJson(throwable);
    }

}
