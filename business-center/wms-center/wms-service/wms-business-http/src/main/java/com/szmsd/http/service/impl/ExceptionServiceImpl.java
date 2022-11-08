package com.szmsd.http.service.impl;

import com.alibaba.fastjson.JSON;
import com.szmsd.http.config.HttpConfig;
import com.szmsd.http.domain.BasCodExternal;
import com.szmsd.http.dto.BasCodExternalDto;
import com.szmsd.http.dto.ExceptionProcessRequest;
import com.szmsd.http.mapper.BasCodExternalMapper;
import com.szmsd.http.service.IExceptionService;
import com.szmsd.http.service.http.WmsRequest;
import com.szmsd.http.vo.ResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExceptionServiceImpl extends WmsRequest implements IExceptionService {
    @Autowired
    private BasCodExternalMapper basCodExternalMapper;
    public ExceptionServiceImpl(HttpConfig httpConfig) {
        super(httpConfig);
    }

    @Override
    public ResponseVO processing(ExceptionProcessRequest exceptionProcessRequest){
        return JSON.parseObject(httpPut(exceptionProcessRequest.getWarehouseCode(), "exception.processing", exceptionProcessRequest), ResponseVO.class);
    }

    @Override
    public List<BasCodExternal> basCodlist(BasCodExternalDto basCodExternalDto) {
        return basCodExternalMapper.selectByPrimaryKey(basCodExternalDto);
    }
}
