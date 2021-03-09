package com.szmsd.http.service.impl;

import com.alibaba.fastjson.JSON;
import com.szmsd.http.config.HttpConfig;
import com.szmsd.http.dto.*;
import com.szmsd.http.service.IOutboundService;
import com.szmsd.http.vo.CreateShipmentResponseVO;
import com.szmsd.http.vo.ResponseVO;
import org.springframework.stereotype.Service;

/**
 * @author zhangyuyuan
 * @date 2021-03-09 11:23
 */
@Service
public class OutboundServiceImpl extends AbstractHttpRequest implements IOutboundService {

    public OutboundServiceImpl(HttpConfig httpConfig) {
        super(httpConfig);
    }

    @Override
    public CreateShipmentResponseVO shipmentCreate(CreateShipmentRequestDto dto) {
        return JSON.parseObject(httpPost(httpConfig.getOutbound().getCreate(), dto), CreateShipmentResponseVO.class);
    }

    @Override
    public ResponseVO shipmentDelete(ShipmentCancelRequestDto dto) {
        return JSON.parseObject(httpDelete(httpConfig.getOutbound().getCancel(), dto), ResponseVO.class);
    }

    @Override
    public ResponseVO shipmentTracking(ShipmentTrackingChangeRequestDto dto) {
        return JSON.parseObject(httpPut(httpConfig.getOutbound().getTracking(), dto), ResponseVO.class);
    }

    @Override
    public ResponseVO shipmentLabel(ShipmentLabelChangeRequestDto dto) {
        return JSON.parseObject(httpPut(httpConfig.getOutbound().getLabel(), dto), ResponseVO.class);
    }

    @Override
    public ResponseVO shipmentShipping(ShipmentUpdateRequestDto dto) {
        return JSON.parseObject(httpPut(httpConfig.getOutbound().getShipping(), dto), ResponseVO.class);
    }
}
