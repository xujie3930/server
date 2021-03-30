package com.szmsd.http.service.impl;

import com.alibaba.fastjson.JSON;
import com.szmsd.common.core.utils.HttpResponseBody;
import com.szmsd.http.config.HttpConfig;
import com.szmsd.http.dto.CreateShipmentOrderCommand;
import com.szmsd.http.dto.ProblemDetails;
import com.szmsd.http.dto.ResponseObject;
import com.szmsd.http.dto.ShipmentOrderResult;
import com.szmsd.http.service.ICarrierService;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * @author zhangyuyuan
 * @date 2021-03-30 11:44
 */
@Service
public class CarrierServiceImpl extends AbstractCarrierServiceHttpRequest implements ICarrierService {

    public CarrierServiceImpl(HttpConfig httpConfig) {
        super(httpConfig);
    }

    @Override
    public void shipmentOrder() {

    }

    @Override
    public void cancellation() {

    }

    @Override
    public ResponseObject.ResponseObjectWrapper<ShipmentOrderResult, ProblemDetails> shipmentOrder(CreateShipmentOrderCommand command) {
        HttpResponseBody responseBody = httpPostBody(httpConfig.getPricedProduct().getPricing(), command);
        ResponseObject.ResponseObjectWrapper<ShipmentOrderResult, ProblemDetails> responseObject = new ResponseObject.ResponseObjectWrapper<>();
        if (HttpStatus.SC_OK == responseBody.getStatus()) {
            responseObject.setSuccess(true);
            responseObject.setObject(JSON.parseObject(responseBody.getBody(), ShipmentOrderResult.class));
        } else {
            responseObject.setError(JSON.parseObject(responseBody.getBody(), ProblemDetails.class));
        }
        return responseObject;
    }
}
