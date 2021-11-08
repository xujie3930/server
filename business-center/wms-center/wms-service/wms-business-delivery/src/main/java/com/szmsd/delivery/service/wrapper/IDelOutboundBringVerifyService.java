package com.szmsd.delivery.service.wrapper;

import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.dto.DelOutboundBringVerifyDto;
import com.szmsd.delivery.vo.DelOutboundBringVerifyVO;
import com.szmsd.http.dto.ChargeWrapper;
import com.szmsd.http.dto.ProblemDetails;
import com.szmsd.http.dto.ResponseObject;
import com.szmsd.http.dto.ShipmentOrderResult;

import java.util.List;

/**
 * 提审业务
 *
 * @author zhangyuyuan
 * @date 2021-03-23 16:33
 */
public interface IDelOutboundBringVerifyService {

    /**
     * 提审
     *
     * @param dto dto
     * @return List<DelOutboundBringVerifyVO>
     */
    List<DelOutboundBringVerifyVO> bringVerify(DelOutboundBringVerifyDto dto);

    /**
     * 初始化
     *
     * @param delOutbound delOutbound
     * @return DelOutboundWrapperContext
     */
    DelOutboundWrapperContext initContext(DelOutbound delOutbound);

    /**
     * 通知修改wms发货指令
     *
     * @param ids
     */
    void updateShipmentLabel(List<String> ids);

    /**
     * 计算包裹费用
     *
     * @param delOutboundWrapperContext delOutboundWrapperContext
     * @param pricingEnum               pricingEnum
     * @return ResponseObject<ChargeWrapper, ProblemDetails>
     */
    ResponseObject<ChargeWrapper, ProblemDetails> pricing(DelOutboundWrapperContext delOutboundWrapperContext, PricingEnum pricingEnum);

    /**
     * 创建承运商物流订单（客户端）
     *
     * @param delOutboundWrapperContext delOutboundWrapperContext
     * @return ShipmentOrderResult
     */
    ShipmentOrderResult shipmentOrder(DelOutboundWrapperContext delOutboundWrapperContext);

    /**
     * 获取承运商标签文件
     *
     * @param delOutbound delOutbound
     * @return boolean，true获取成功，false获取失败
     */
    boolean getShipmentLabel(DelOutbound delOutbound);

    /**
     * 传输标签文件给WMS
     *
     * @param delOutbound delOutbound
     */
    void htpShipmentLabel(DelOutbound delOutbound);

    /**
     * 创建发货指令
     *
     * @param delOutbound delOutbound
     */
    void shipmentShipping(DelOutbound delOutbound);

    /**
     * 取消承运商物流订单（客户端）
     *
     * @param warehouseCode       warehouseCode
     * @param referenceNumber     referenceNumber
     * @param shipmentOrderNumber shipmentOrderNumber
     * @param trackingNo          trackingNo
     */
    void cancellation(String warehouseCode, String referenceNumber, String shipmentOrderNumber, String trackingNo);

    /**
     * 创建出库单
     *
     * @param delOutboundWrapperContext delOutboundWrapperContext
     * @param trackingNo                trackingNo
     * @return String
     */
    String shipmentCreate(DelOutboundWrapperContext delOutboundWrapperContext, String trackingNo);
}
