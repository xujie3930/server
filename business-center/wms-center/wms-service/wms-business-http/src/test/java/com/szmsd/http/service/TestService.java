package com.szmsd.http.service;

import com.szmsd.http.dto.CreateShipmentRequestDto;
import com.szmsd.http.vo.ResponseVO;

/**
 * @author zhangyuyuan
 * @date 2021-03-11 9:28
 */
public interface TestService {

    public ResponseVO create(CreateShipmentRequestDto dto);
}
