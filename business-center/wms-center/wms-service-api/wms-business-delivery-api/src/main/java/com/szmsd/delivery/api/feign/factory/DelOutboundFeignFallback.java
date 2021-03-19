package com.szmsd.delivery.api.feign.factory;

import com.szmsd.common.core.domain.R;
import com.szmsd.delivery.api.feign.DelOutboundFeignService;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.dto.PackageMeasureRequestDto;
import com.szmsd.delivery.dto.ShipmentContainersRequestDto;
import com.szmsd.delivery.dto.ShipmentPackingMaterialRequestDto;
import com.szmsd.delivery.dto.ShipmentRequestDto;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author zhangyuyuan
 * @date 2021-03-06 14:33
 */
@Component
public class DelOutboundFeignFallback implements FallbackFactory<DelOutboundFeignService> {
    @Override
    public DelOutboundFeignService create(Throwable throwable) {
        return new DelOutboundFeignService() {
            @Override
            public R<Integer> shipment(ShipmentRequestDto dto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<Integer> shipmentMeasure(PackageMeasureRequestDto dto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<Integer> shipmentPacking(ShipmentPackingMaterialRequestDto dto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<Integer> shipmentContainers(ShipmentContainersRequestDto dto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<DelOutbound> details(String orderId) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
