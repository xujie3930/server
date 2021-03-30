package com.szmsd.http.api.service.impl;

import com.szmsd.common.core.domain.R;
import com.szmsd.http.api.feign.HtpCarrierFeignService;
import com.szmsd.http.api.service.IHtpCarrierClientService;
import com.szmsd.http.dto.CreateShipmentOrderCommand;
import com.szmsd.http.dto.ProblemDetails;
import com.szmsd.http.dto.ResponseObject;
import com.szmsd.http.dto.ShipmentOrderResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhangyuyuan
 * @date 2021-03-30 11:52
 */
@Service
public class HtpCarrierClientServiceImpl implements IHtpCarrierClientService {

    @Autowired
    private HtpCarrierFeignService htpCarrierFeignService;

    @Override
    public ResponseObject.ResponseObjectWrapper<ShipmentOrderResult, ProblemDetails> shipmentOrder(CreateShipmentOrderCommand command) {
        return R.getDataAndException(this.htpCarrierFeignService.shipmentOrder(command));
    }
}
