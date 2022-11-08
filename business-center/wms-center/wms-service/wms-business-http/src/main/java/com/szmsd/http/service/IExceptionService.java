package com.szmsd.http.service;

import com.szmsd.http.domain.BasCodExternal;
import com.szmsd.http.dto.BasCodExternalDto;
import com.szmsd.http.dto.ExceptionProcessRequest;
import com.szmsd.http.dto.ProductRequest;
import com.szmsd.http.vo.ResponseVO;

import java.util.List;

public interface IExceptionService {

    ResponseVO processing(ExceptionProcessRequest exceptionProcessRequest);

    List<BasCodExternal>  basCodlist(BasCodExternalDto basCodExternalDto);
}
