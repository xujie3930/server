package com.szmsd.finance.api.feign.factory;

import com.szmsd.common.core.domain.R;
import com.szmsd.finance.api.feign.RefundRequestFeignService;
import com.szmsd.finance.dto.RefundRequestListAutoDTO;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RefundRequestFeignFallback implements FallbackFactory<RefundRequestFeignService> {
    @Override
    public RefundRequestFeignService create(Throwable throwable) {
        return new RefundRequestFeignService() {

            @Override
            public R autoRefund(RefundRequestListAutoDTO refundReviewDTO) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
