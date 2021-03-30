package com.szmsd.delivery.service.wrapper;

import com.szmsd.delivery.dto.ShipmentPackingMaterialRequestDto;

/**
 * @author zhangyuyuan
 * @date 2021-03-30 14:52
 */
public interface IDelOutboundOpenService {

    /**
     * 出库管理 - Open - 接收出库包裹使用包材
     *
     * @param dto dto
     * @return int
     */
    int shipmentPacking(ShipmentPackingMaterialRequestDto dto);
}
