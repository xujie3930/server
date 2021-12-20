package com.szmsd.http.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.szmsd.http.config.HttpConfig;
import com.szmsd.http.dto.*;
import com.szmsd.http.service.IOutboundService;
import com.szmsd.http.service.http.WmsRequest;
import com.szmsd.http.vo.CreateShipmentResponseVO;
import com.szmsd.http.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author zhangyuyuan
 * @date 2021-03-09 11:23
 */
@Slf4j
@Service
public class OutboundServiceImpl extends WmsRequest implements IOutboundService {

    public OutboundServiceImpl(HttpConfig httpConfig) {
        super(httpConfig);
    }

    @Override
    public CreateShipmentResponseVO shipmentCreate(CreateShipmentRequestDto dto) {
        log.info("shipmentCreate：{}", JSONObject.toJSONString(dto));
        String text = httpPost(dto.getWarehouseCode(), "outbound.create", dto);
        try {
            return JSON.parseObject(text, CreateShipmentResponseVO.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            CreateShipmentResponseVO createShipmentResponseVO = new CreateShipmentResponseVO();
            createShipmentResponseVO.setSuccess(false);
            createShipmentResponseVO.setMessage(text);
            return createShipmentResponseVO;
        }
    }

    @Override
    public ResponseVO shipmentDelete(ShipmentCancelRequestDto dto) {
        String text = httpDelete(dto.getWarehouseCode(), "outbound.cancel", dto);
        return ResponseVO.build(text);
    }

    @Override
    public ResponseVO shipmentTracking(ShipmentTrackingChangeRequestDto dto) {
        String text = httpPut(dto.getWarehouseCode(), "outbound.tracking", dto);
        return ResponseVO.build(text);
    }

    @Override
    public ResponseVO shipmentLabel(ShipmentLabelChangeRequestDto dto) {
        String text = httpPut(dto.getWarehouseCode(), "outbound.label", dto);
        return ResponseVO.build(text);
    }

    @Override
    public ResponseVO shipmentShipping(ShipmentUpdateRequestDto dto) {
        String text = httpPut(dto.getWarehouseCode(), "outbound.shipping", dto);
        return ResponseVO.build(text);
    }
}
