package com.szmsd.delivery.api.service;

import com.szmsd.delivery.dto.*;
import com.szmsd.delivery.vo.DelOutboundAddResponse;
import com.szmsd.delivery.vo.DelOutboundLabelResponse;
import com.szmsd.http.vo.PricedProduct;

import java.util.List;

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

    /**
     * 取消出库单
     *
     * @param dto dto
     * @return int
     */
    int canceled(DelOutboundCanceledDto dto);

    /**
     * 物流服务
     *
     * @param dto dto
     * @return PricedProduct
     */
    List<PricedProduct> inService(DelOutboundOtherInServiceDto dto);

    /**
     * 出库单新增 - DOC支持
     *
     * @param dto dto
     * @return DelOutboundAddResponse
     */
    List<DelOutboundAddResponse> add(List<DelOutboundDto> dto);

    /**
     * 获取标签
     *
     * @param dto dto
     * @return DelOutboundLabelResponse
     */
    List<DelOutboundLabelResponse> labelBase64(DelOutboundLabelDto dto);

    /**
     * 上传箱标
     *
     * @param dto dto
     * @return int
     */
    int uploadBoxLabel(DelOutboundUploadBoxLabelDto dto);
}
