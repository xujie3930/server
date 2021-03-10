package com.szmsd.delivery.service.wrapper;

import com.szmsd.delivery.dto.DelOutboundDto;
import com.szmsd.http.dto.ShipmentCancelRequestDto;
import com.szmsd.http.dto.ShipmentLabelChangeRequestDto;
import com.szmsd.http.dto.ShipmentTrackingChangeRequestDto;
import com.szmsd.http.dto.ShipmentUpdateRequestDto;

/**
 * @author zhangyuyuan
 * @date 2021-03-09 16:48
 */
public interface IDelOutboundHttpWrapperService {

    /**
     * 创建出库单
     *
     * @param dto        dto
     * @param refOrderNo refOrderNo
     * @return String
     */
    String shipmentCreate(DelOutboundDto dto, String refOrderNo);

    /**
     * 取消出库单
     *
     * @param dto dto
     * @return Boolean
     */
    Boolean shipmentDelete(ShipmentCancelRequestDto dto);

    /**
     * 更新出库单挂号
     *
     * @param dto dto
     * @return Boolean
     */
    Boolean shipmentTracking(ShipmentTrackingChangeRequestDto dto);

    /**
     * 更新出库单标签
     *
     * @param dto dto
     * @return Boolean
     */
    Boolean shipmentLabel(ShipmentLabelChangeRequestDto dto);

    /**
     * 更新出库单发货指令
     *
     * @param dto dto
     * @return Boolean
     */
    Boolean shipmentShipping(ShipmentUpdateRequestDto dto);
}
