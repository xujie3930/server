package com.szmsd.http.service.impl;

import com.alibaba.fastjson.JSON;
import com.szmsd.common.core.utils.FileStream;
import com.szmsd.common.core.utils.HttpResponseBody;
import com.szmsd.http.config.HttpConfig;
import com.szmsd.http.dto.*;
import com.szmsd.http.service.ICarrierService;
import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;

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
    public ResponseObject.ResponseObjectWrapper<ShipmentOrderResult, ProblemDetails> shipmentOrder(CreateShipmentOrderCommand command) {
        HttpResponseBody responseBody = httpPostBody(httpConfig.getCarrierService().getShipmentOrder(), command);
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
        HttpResponseBody responseBody = httpPostBody(httpConfig.getCarrierService().getCancellation(), command);
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
    public ResponseObject.ResponseObjectWrapper<FileStream, ProblemDetails> label(String orderNumber) {
        HttpResponseBody httpResponseBody = httpGetFile(MessageFormat.format(httpConfig.getCarrierService().getLabel(), orderNumber), null);
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
