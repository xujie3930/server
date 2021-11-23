package com.szmsd.http.service.impl;

import com.alibaba.fastjson.JSON;
import com.szmsd.common.core.utils.FileStream;
import com.szmsd.common.core.utils.HttpResponseBody;
import com.szmsd.http.config.HttpConfig;
import com.szmsd.http.dto.*;
import com.szmsd.http.service.ICarrierService;
import com.szmsd.http.service.http.SaaSCarrierServiceAdminRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * @author zhangyuyuan
 * @date 2021-03-30 11:44
 */
@Service
public class CarrierServiceImpl extends SaaSCarrierServiceAdminRequest implements ICarrierService {

    public CarrierServiceImpl(HttpConfig httpConfig) {
        super(httpConfig);
    }

    @Override
    public ResponseObject.ResponseObjectWrapper<ShipmentOrderResult, ProblemDetails> shipmentOrder(CreateShipmentOrderCommand command) {
        HttpResponseBody responseBody = httpPostBody(command.getWarehouseCode(), "shipment-order.create", command);
        ResponseObject.ResponseObjectWrapper<ShipmentOrderResult, ProblemDetails> responseObject = new ResponseObject.ResponseObjectWrapper<>();
        if (HttpStatus.SC_OK == responseBody.getStatus()) {
            responseObject.setSuccess(true);
            responseObject.setObject(JSON.parseObject(responseBody.getBody(), ShipmentOrderResult.class));
        } else {
            responseObject.setError(JSON.parseObject(responseBody.getBody(), ProblemDetails.class));
        }
        return responseObject;
    }

    @Override
    public ResponseObject.ResponseObjectWrapper<CancelShipmentOrderBatchResult, ErrorDataDto> cancellation(CancelShipmentOrderCommand command) {
        HttpResponseBody responseBody = httpPostBody(command.getWarehouseCode(), "shipment-order.cancellation", command);
        ResponseObject.ResponseObjectWrapper<CancelShipmentOrderBatchResult, ErrorDataDto> responseObject = new ResponseObject.ResponseObjectWrapper<>();
        if (HttpStatus.SC_OK == responseBody.getStatus()) {
            responseObject.setSuccess(true);
            responseObject.setObject(JSON.parseObject(responseBody.getBody(), CancelShipmentOrderBatchResult.class));
        } else {
            responseObject.setError(JSON.parseObject(responseBody.getBody(), ErrorDataDto.class));
        }
        return responseObject;
    }

    @Override
    public ResponseObject.ResponseObjectWrapper<FileStream, ProblemDetails> label(CreateShipmentOrderCommand command) {
        String shipmentOrderLabelUrl = command.getShipmentOrderLabelUrl();
        HttpResponseBody httpResponseBody;
        if (StringUtils.isNotEmpty(shipmentOrderLabelUrl)) {
            httpResponseBody = httpGetFile(command.getWarehouseCode(), "shipment-order.label-url", null, shipmentOrderLabelUrl);
        } else {
            httpResponseBody = httpGetFile(command.getWarehouseCode(), "shipment-order.label", null, command.getOrderNumber());
        }
        ResponseObject.ResponseObjectWrapper<FileStream, ProblemDetails> responseObject = new ResponseObject.ResponseObjectWrapper<>();
        if (httpResponseBody instanceof HttpResponseBody.HttpResponseByteArrayWrapper) {
            HttpResponseBody.HttpResponseByteArrayWrapper httpResponseByteArrayWrapper = (HttpResponseBody.HttpResponseByteArrayWrapper) httpResponseBody;
            if (HttpStatus.SC_OK == httpResponseByteArrayWrapper.getStatus()) {
                responseObject.setSuccess(true);
                FileStream fileStream = new FileStream();
                fileStream.setInputStream(httpResponseByteArrayWrapper.getByteArray());
                responseObject.setObject(fileStream);
            } else {
                byte[] byteArray = httpResponseByteArrayWrapper.getByteArray();
                String text = new String(byteArray, StandardCharsets.UTF_8);
                responseObject.setError(JSON.parseObject(text, ProblemDetails.class));
            }
        }
        return responseObject;
    }
}
