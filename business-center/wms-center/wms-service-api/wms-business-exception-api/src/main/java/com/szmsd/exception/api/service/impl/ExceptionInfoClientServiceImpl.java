package com.szmsd.exception.api.service.impl;

import com.szmsd.common.core.domain.R;
import com.szmsd.exception.api.feign.ExceptionInfoFeignService;
import com.szmsd.exception.api.service.ExceptionInfoClientService;
import com.szmsd.exception.dto.NewExceptionRequest;
import com.szmsd.exception.dto.ProcessExceptionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExceptionInfoClientServiceImpl implements ExceptionInfoClientService {

    @Autowired
    private ExceptionInfoFeignService exceptionInfoFeignService;
    @Override
    public R newException(NewExceptionRequest newExceptionRequest) {
        return exceptionInfoFeignService.newException(newExceptionRequest);
    }

    @Override
    public R processException(ProcessExceptionRequest processExceptionRequest) {
        return exceptionInfoFeignService.processException(processExceptionRequest);
    }
}
