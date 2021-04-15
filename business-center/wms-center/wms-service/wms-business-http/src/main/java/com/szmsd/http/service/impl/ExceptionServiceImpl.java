package com.szmsd.http.service.impl;

import com.alibaba.fastjson.JSON;
import com.szmsd.http.config.HttpConfig;
import com.szmsd.http.dto.ExceptionProcessRequest;
import com.szmsd.http.service.IExceptionService;
import com.szmsd.http.service.http.WmsRequest;
import com.szmsd.http.vo.ResponseVO;
import org.springframework.stereotype.Service;

@Service
public class ExceptionServiceImpl extends WmsRequest implements IExceptionService {
    public ExceptionServiceImpl(HttpConfig httpConfig) {
        super(httpConfig);
    }

    @Override
    public ResponseVO processing(ExceptionProcessRequest exceptionProcessRequest){
        return JSON.parseObject(httpPut("", "exception.processing", exceptionProcessRequest), ResponseVO.class);
    }
}
