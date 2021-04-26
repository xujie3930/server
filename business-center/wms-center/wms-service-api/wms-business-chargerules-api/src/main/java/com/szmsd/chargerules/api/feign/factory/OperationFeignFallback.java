package com.szmsd.chargerules.api.feign.factory;

import com.szmsd.chargerules.api.feign.OperationFeignService;
import com.szmsd.common.core.domain.R;
import com.szmsd.delivery.vo.DelOutboundVO;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class OperationFeignFallback implements FallbackFactory<OperationFeignService> {


    @Override
    public OperationFeignService create(Throwable throwable) {
        return new OperationFeignService() {
            @Override
            public R delOutboundCharge(DelOutboundVO delOutboundVO) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R delOutboundThaw(DelOutboundVO delOutboundVO) {
                return null;
            }

            @Override
            public R delOutboundFreeze(DelOutboundVO delOutboundVO) {
                return null;
            }
        };
    }
}
