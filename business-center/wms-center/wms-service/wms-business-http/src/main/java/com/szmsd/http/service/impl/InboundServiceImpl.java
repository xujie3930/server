package com.szmsd.http.service.impl;

import com.alibaba.fastjson.JSON;
import com.szmsd.http.config.HttpConfig;
import com.szmsd.http.dto.CancelReceiptRequest;
import com.szmsd.http.dto.CreatePackageReceiptRequest;
import com.szmsd.http.dto.CreateReceiptRequest;
import com.szmsd.http.dto.CreateTrackRequest;
import com.szmsd.http.service.IInboundService;
import com.szmsd.http.service.http.WmsRequest;
import com.szmsd.http.vo.CreateReceiptResponse;
import com.szmsd.http.vo.CreateTrackingResponse;
import com.szmsd.http.vo.ResponseVO;
import org.springframework.stereotype.Service;

@Service
public class InboundServiceImpl extends WmsRequest implements IInboundService {

    public InboundServiceImpl(HttpConfig httpConfig) {
        super(httpConfig);
    }

    @Override
    public CreateReceiptResponse create(CreateReceiptRequest createReceiptRequestDTO) {
        return JSON.parseObject(httpPost(createReceiptRequestDTO.getWarehouseCode(), "inbound.create", createReceiptRequestDTO), CreateReceiptResponse.class);
    }

    @Override
    public ResponseVO cancel(CancelReceiptRequest cancelReceiptRequestDTO) {
        return JSON.parseObject(httpDelete(cancelReceiptRequestDTO.getWarehouseCode(), "inbound.cancel", cancelReceiptRequestDTO), CreateReceiptResponse.class);
    }

    @Override
    public ResponseVO createPackage(CreatePackageReceiptRequest createPackageReceiptRequest) {
        return JSON.parseObject(httpPost(createPackageReceiptRequest.getWarehouseCode(), "inbound.createPackage", createPackageReceiptRequest), CreateReceiptResponse.class);
    }

    @Override
    public ResponseVO createTracking(CreateTrackRequest createTrackRequest) {
        return  JSON.parseObject(httpPost(createTrackRequest.getWarehouseCode(), "inbound.createTracking", createTrackRequest), CreateTrackingResponse.class);
    }
}
