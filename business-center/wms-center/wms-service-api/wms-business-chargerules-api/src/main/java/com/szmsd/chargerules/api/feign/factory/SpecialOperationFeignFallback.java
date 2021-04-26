package com.szmsd.chargerules.api.feign.factory;

import com.szmsd.chargerules.api.feign.SpecialOperationFeignService;
import com.szmsd.chargerules.dto.BasSpecialOperationRequestDTO;
import com.szmsd.common.core.domain.R;
import com.szmsd.delivery.vo.DelOutboundVO;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class SpecialOperationFeignFallback implements FallbackFactory<SpecialOperationFeignService> {

    @Override
    public SpecialOperationFeignService create(Throwable throwable) {

        return new SpecialOperationFeignService() {

            @Override
            public R<Boolean> specialOperation(BasSpecialOperationRequestDTO baseProduct) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<Boolean> delOutboundCharge(DelOutboundVO delOutboundVO) {
                return R.convertResultJson(throwable);
            }

        };
    }
}
