package com.szmsd.http.api.feign.fallback;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.api.feign.HtpCarrierFeignService;
import com.szmsd.http.dto.CreateShipmentOrderCommand;
import com.szmsd.http.dto.ProblemDetails;
import com.szmsd.http.dto.ResponseObject;
import com.szmsd.http.dto.ShipmentOrderResult;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @author zhangyuyuan
 * @date 2021-03-30 11:51
 */
@Component
public class HtpCarrierFeignFallback implements FallbackFactory<HtpCarrierFeignService> {

    @Override
    public HtpCarrierFeignService create(Throwable throwable) {
        return new HtpCarrierFeignService() {
            @Override
            public R<ResponseObject.ResponseObjectWrapper<ShipmentOrderResult, ProblemDetails>> shipmentOrder(CreateShipmentOrderCommand command) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
