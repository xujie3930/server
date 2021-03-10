package com.szmsd.http.service.impl;

import com.alibaba.fastjson.JSON;
import com.szmsd.http.config.HttpConfig;
import com.szmsd.http.dto.CancelReceiptRequest;
import com.szmsd.http.dto.CreateReceiptRequest;
import com.szmsd.http.service.IInboundService;
import com.szmsd.http.vo.CreateReceiptResponse;
import com.szmsd.http.vo.ResponseVO;
import org.springframework.stereotype.Service;

@Service
public class InboundServiceImpl extends AbstractBaseHttpRequest implements IInboundService {

    public InboundServiceImpl(HttpConfig httpConfig) {
        super(httpConfig);
    }

    @Override
    public CreateReceiptResponse create(CreateReceiptRequest createReceiptRequestDTO) {
        return JSON.parseObject(httpPost(httpConfig.getInbound().getCreate(), createReceiptRequestDTO), CreateReceiptResponse.class);
    }

    @Override
    public ResponseVO cancel(CancelReceiptRequest cancelReceiptRequestDTO) {
        return JSON.parseObject(httpDelete(httpConfig.getInbound().getCancel(), cancelReceiptRequestDTO), CreateReceiptResponse.class);
    }
}
