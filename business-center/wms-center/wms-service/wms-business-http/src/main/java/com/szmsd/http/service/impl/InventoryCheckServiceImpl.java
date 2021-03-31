package com.szmsd.http.service.impl;


import com.alibaba.fastjson.JSON;
import com.szmsd.http.config.HttpConfig;
import com.szmsd.http.dto.CountingRequest;
import com.szmsd.http.service.IInventoryCheckService;
import com.szmsd.http.vo.ResponseVO;
import org.springframework.stereotype.Service;

@Service
public class InventoryCheckServiceImpl extends AbstractBaseHttpRequest implements IInventoryCheckService {

    public InventoryCheckServiceImpl(HttpConfig httpConfig) {
        super(httpConfig);
    }

    @Override
    public ResponseVO counting(CountingRequest countingRequest) {
        return JSON.parseObject(httpPost(httpConfig.getInventoryInfo().getCounting(), countingRequest), ResponseVO.class);
    }

}
