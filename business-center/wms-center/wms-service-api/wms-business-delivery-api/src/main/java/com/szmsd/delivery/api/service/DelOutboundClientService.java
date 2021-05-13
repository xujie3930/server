package com.szmsd.delivery.api.service;

import com.szmsd.delivery.dto.*;

/**
 * @author zhangyuyuan
 * @date 2021-03-06 14:35
 */
public interface DelOutboundClientService {

    /**
     * 出库管理 - Open - 接收出库单状态
     *
     * @param dto dto
     * @return int
     */
    int shipment(ShipmentRequestDto dto);

    /**
     * 出库管理 - Open - 接收出库包裹使用包材
     *
     * @param dto dto
     * @return Integer
     */
    int shipmentPacking(ShipmentPackingMaterialRequestDto dto);

    /**
     * 出库管理 - Open - 接收批量出库单类型装箱信息
     *
     * @param dto dto
     * @return Integer
     */
    int shipmentContainers(ShipmentContainersRequestDto dto);

    /**
     * 继续处理
     *
     * @param dto dto
     * @return int
     */
    int furtherHandler(DelOutboundFurtherHandlerDto dto);
}
