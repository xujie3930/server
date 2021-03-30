package com.szmsd.http.service;

import com.szmsd.http.dto.ExceptionProcessRequest;
import com.szmsd.http.dto.ProductRequest;
import com.szmsd.http.vo.ResponseVO;

public interface IExceptionService {

    ResponseVO processing(ExceptionProcessRequest exceptionProcessRequest);
}
