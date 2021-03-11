package com.szmsd.http.service;

import com.szmsd.http.dto.CreateShipmentRequestDto;
import com.szmsd.http.vo.ResponseVO;

/**
 * @author zhangyuyuan
 * @date 2021-03-11 9:29
 */
public class TestServiceImpl implements TestService {

    @Override
    public ResponseVO create(CreateShipmentRequestDto dto) {

        System.out.println(dto.getWarehouseCode());

        ResponseVO vo = new ResponseVO();
        vo.setMessage(dto.getOrderType());

        return vo;
    }
}
