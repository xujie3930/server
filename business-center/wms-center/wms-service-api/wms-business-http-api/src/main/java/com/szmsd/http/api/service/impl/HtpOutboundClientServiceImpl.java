package com.szmsd.http.api.service.impl;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.api.feign.HtpOutboundFeignService;
import com.szmsd.http.api.service.IHtpOutboundClientService;
import com.szmsd.http.dto.*;
import com.szmsd.http.vo.CreateShipmentResponseVO;
import com.szmsd.http.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhangyuyuan
 * @date 2021-03-09 11:35
 */
@Service
public class HtpOutboundClientServiceImpl implements IHtpOutboundClientService {

    @Autowired
    private HtpOutboundFeignService htpOutboundFeignService;

    @Override
    public CreateShipmentResponseVO shipmentCreate(CreateShipmentRequestDto dto) {
        return R.getDataAndException(this.htpOutboundFeignService.shipmentCreate(dto));
    }

    @Override
    public ResponseVO shipmentDelete(ShipmentCancelRequestDto dto) {
        return R.getDataAndException(this.htpOutboundFeignService.shipmentDelete(dto));
    }

    @Override
    public ResponseVO shipmentTracking(ShipmentTrackingChangeRequestDto dto) {
        return R.getDataAndException(this.htpOutboundFeignService.shipmentTracking(dto));
    }

    @Override
    public ResponseVO shipmentLabel(ShipmentLabelChangeRequestDto dto) {
        return R.getDataAndException(this.htpOutboundFeignService.shipmentLabel(dto));
    }

    @Override
    public ResponseVO shipmentShipping(ShipmentUpdateRequestDto dto) {
        return R.getDataAndException(this.htpOutboundFeignService.shipmentShipping(dto));
    }

    @Override
    public ResponseVO shipmentMultiboxrelation(ShipmentMultiboxrelationRequestDto dto) {
        return R.getDataAndException(this.htpOutboundFeignService.shipmentMultiboxrelation(dto));
    }
}
