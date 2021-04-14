package com.szmsd.http.service.impl;

import com.alibaba.fastjson.JSON;
import com.szmsd.http.config.HttpConfig;
import com.szmsd.http.dto.*;
import com.szmsd.http.service.IOutboundService;
import com.szmsd.http.service.http.WmsRequest;
import com.szmsd.http.vo.CreateShipmentResponseVO;
import com.szmsd.http.vo.ResponseVO;
import org.springframework.stereotype.Service;

/**
 * @author zhangyuyuan
 * @date 2021-03-09 11:23
 */
@Service
public class OutboundServiceImpl extends WmsRequest implements IOutboundService {

    public OutboundServiceImpl(HttpConfig httpConfig) {
        super(httpConfig);
    }

    @Override
    public CreateShipmentResponseVO shipmentCreate(CreateShipmentRequestDto dto) {
        return JSON.parseObject(httpPost(dto.getWarehouseCode(), "outbound.create", dto), CreateShipmentResponseVO.class);
    }

    @Override
    public ResponseVO shipmentDelete(ShipmentCancelRequestDto dto) {
        return JSON.parseObject(httpDelete(dto.getWarehouseCode(), "outbound.cancel", dto), ResponseVO.class);
    }

    @Override
    public ResponseVO shipmentTracking(ShipmentTrackingChangeRequestDto dto) {
        return JSON.parseObject(httpPut(dto.getWarehouseCode(), "outbound.tracking", dto), ResponseVO.class);
    }

    @Override
    public ResponseVO shipmentLabel(ShipmentLabelChangeRequestDto dto) {
        return JSON.parseObject(httpPut(dto.getWarehouseCode(), "outbound.label", dto), ResponseVO.class);
    }

    @Override
    public ResponseVO shipmentShipping(ShipmentUpdateRequestDto dto) {
        return JSON.parseObject(httpPut(dto.getWarehouseCode(), "outbound.shipping", dto), ResponseVO.class);
    }
}
