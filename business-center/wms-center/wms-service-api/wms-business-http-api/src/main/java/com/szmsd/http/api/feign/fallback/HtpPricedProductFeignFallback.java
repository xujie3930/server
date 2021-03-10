package com.szmsd.http.api.feign.fallback;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.api.BusinessHttpInterface;
import com.szmsd.http.api.feign.HtpPricedProductFeignService;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HtpPricedProductFeignFallback implements FallbackFactory<HtpPricedProductFeignService> {
    @Override
    public HtpPricedProductFeignService create(Throwable throwable) {
        log.info("{}服务调用失败：{}", BusinessHttpInterface.SERVICE_NAME, throwable.getMessage());
        return getPricedProductsCommand -> R.convertResultJson(throwable);
    }
}
