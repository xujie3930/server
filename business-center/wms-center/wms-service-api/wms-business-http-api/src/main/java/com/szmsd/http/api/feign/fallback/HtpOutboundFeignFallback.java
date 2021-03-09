package com.szmsd.http.api.feign.fallback;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.api.feign.HtpOutboundFeignService;
import com.szmsd.http.dto.*;
import com.szmsd.http.vo.CreateShipmentResponseVO;
import com.szmsd.http.vo.ResponseVO;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author zhangyuyuan
 * @date 2021-03-09 11:33
 */
@Component
public class HtpOutboundFeignFallback implements FallbackFactory<HtpOutboundFeignService> {
    @Override
    public HtpOutboundFeignService create(Throwable throwable) {
        return new HtpOutboundFeignService() {
            @Override
            public R<CreateShipmentResponseVO> shipmentCreate(CreateShipmentRequestDto dto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<ResponseVO> shipmentDelete(ShipmentCancelRequestDto dto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<ResponseVO> shipmentTracking(ShipmentTrackingChangeRequestDto dto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<ResponseVO> shipmentLabel(ShipmentLabelChangeRequestDto dto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<ResponseVO> shipmentShipping(ShipmentUpdateRequestDto dto) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
