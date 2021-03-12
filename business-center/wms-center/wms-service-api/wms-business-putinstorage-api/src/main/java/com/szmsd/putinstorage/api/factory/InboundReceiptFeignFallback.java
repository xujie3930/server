package com.szmsd.putinstorage.api.factory;

import com.szmsd.common.core.domain.R;
import com.szmsd.putinstorage.api.feign.InboundReceiptFeignService;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class InboundReceiptFeignFallback implements FallbackFactory<InboundReceiptFeignService> {
    @Override
    public InboundReceiptFeignService create(Throwable throwable) {
        return receivingRequest -> R.convertResultJson(throwable);
    }
}
