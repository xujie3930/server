package com.szmsd.http.service.impl;

import com.alibaba.fastjson.JSON;
import com.szmsd.http.config.HttpConfig;
import com.szmsd.http.dto.ExceptionProcessRequest;
import com.szmsd.http.dto.ProductRequest;
import com.szmsd.http.service.IExceptionService;
import com.szmsd.http.vo.ResponseVO;
import org.springframework.stereotype.Service;

@Service
public class ExceptionServiceImpl extends AbstractBaseHttpRequest implements IExceptionService {
    public ExceptionServiceImpl(HttpConfig httpConfig) {
        super(httpConfig);
    }

    @Override
    public ResponseVO processing(ExceptionProcessRequest exceptionProcessRequest){
        return JSON.parseObject(httpPut(httpConfig.getExceptionInfo().getProcessing(), exceptionProcessRequest), ResponseVO.class);
    }
}
