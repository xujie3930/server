package com.szmsd.http.service.impl;

import com.alibaba.fastjson.JSON;
import com.szmsd.http.config.HttpConfig;
import com.szmsd.http.dto.*;
import com.szmsd.http.service.IBasService;
import com.szmsd.http.vo.ResponseVO;
import org.springframework.stereotype.Service;

@Service
public class BasServiceImpl extends AbstractBaseHttpRequest implements IBasService {

    public BasServiceImpl(HttpConfig httpConfig) {
        super(httpConfig);
    }

    @Override
    public ResponseVO createPacking(PackingRequest packingRequest){
        return JSON.parseObject(httpPost(httpConfig.getBas().getPacking(), packingRequest), ResponseVO.class);
    }
    @Override
    public ResponseVO createProduct(ProductRequest productRequest){
        return JSON.parseObject(httpPost(httpConfig.getBas().getProducts(), productRequest), ResponseVO.class);
    }

    @Override
    public ResponseVO createSeller(SellerRequest sellerRequest){
        return JSON.parseObject(httpPost(httpConfig.getBas().getSeller(), sellerRequest), ResponseVO.class);
    }

    @Override
    public ResponseVO save(SpecialOperationRequest specialOperationRequest) {
        return JSON.parseObject(httpPost(httpConfig.getBas().getSpecialOperationType(), specialOperationRequest), ResponseVO.class);
    }

    @Override
    public ResponseVO update(SpecialOperationResultRequest specialOperationResultRequest) {
        return JSON.parseObject(httpPost(httpConfig.getBas().getSpecialOperationResult(), specialOperationResultRequest), ResponseVO.class);
    }

}
