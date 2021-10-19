package com.szmsd.chargerules.api.feign.factory;

import com.szmsd.chargerules.api.feign.OperationFeignService;
import com.szmsd.chargerules.domain.Operation;
import com.szmsd.chargerules.dto.OperationDTO;
import com.szmsd.common.core.domain.R;
import com.szmsd.delivery.vo.DelOutboundOperationVO;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class OperationFeignFallback implements FallbackFactory<OperationFeignService> {


    @Override
    public OperationFeignService create(Throwable throwable) {
        return new OperationFeignService() {
            @Override
            public R delOutboundCharge(DelOutboundOperationVO delOutboundVO) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<Operation> queryDetails(OperationDTO operationDTO) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R delOutboundThaw(DelOutboundOperationVO delOutboundVO) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R delOutboundFreeze(DelOutboundOperationVO delOutboundVO) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
