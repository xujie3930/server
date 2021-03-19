package com.szmsd.http.api.feign.fallback;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.api.BusinessHttpInterface;
import com.szmsd.http.api.feign.HtpShipmentOrderFeignService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HtpShipmentOrderFeignFallback implements FallbackFactory<HtpShipmentOrderFeignService> {
    @Override
    public HtpShipmentOrderFeignService create(Throwable throwable) {
        log.error("{}服务调用失败：{}", BusinessHttpInterface.SERVICE_NAME, throwable.getMessage());
        return () -> R.convertResultJson(throwable);
    }
}
