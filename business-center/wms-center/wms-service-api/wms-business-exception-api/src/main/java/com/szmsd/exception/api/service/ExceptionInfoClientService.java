package com.szmsd.exception.api.service;

import com.szmsd.common.core.domain.R;
import com.szmsd.exception.dto.NewExceptionRequest;
import com.szmsd.exception.dto.ProcessExceptionRequest;

public interface ExceptionInfoClientService {
    /**
     * 接收仓库创建的异常单
     * @param newExceptionRequest
     * @return
     */
    R newException(NewExceptionRequest newExceptionRequest);

    /**
     * 接收仓库异常单的处理
     * @param processExceptionRequest
     * @return
     */
    R processException(ProcessExceptionRequest processExceptionRequest);
}
