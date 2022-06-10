package com.szmsd.http.service;

import com.szmsd.http.dto.*;
import com.szmsd.http.vo.CreateShipmentResponseVO;
import com.szmsd.http.vo.ResponseVO;

/**
 * @author zhangyuyuan
 * @date 2021-03-09 10:49
 */
public interface IOutboundService {

    /**
     * 创建出库单
     *
     * @param dto dto
     * @return CreateShipmentResponseVO
     */
    CreateShipmentResponseVO shipmentCreate(CreateShipmentRequestDto dto);

    /**
     * 取消出库单
     *
     * @param dto dto
     * @return ResponseVO
     */
    ResponseVO shipmentDelete(ShipmentCancelRequestDto dto);

    /**
     * 更新出库单挂号
     *
     * @param dto dto
     * @return ResponseVO
     */
    ResponseVO shipmentTracking(ShipmentTrackingChangeRequestDto dto);

    /**
     * 更新出库单标签
     *
     * @param dto dto
     * @return ResponseVO
     */
    ResponseVO shipmentLabel(ShipmentLabelChangeRequestDto dto);

    /**
     * 更新出库单发货指令
     *
     * @param dto dto
     * @return ResponseVO
     */
    ResponseVO shipmentShipping(ShipmentUpdateRequestDto dto);

    ResponseVO shipmentMultiboxrelation(ShipmentMultiboxrelationRequestDto dto);
}
