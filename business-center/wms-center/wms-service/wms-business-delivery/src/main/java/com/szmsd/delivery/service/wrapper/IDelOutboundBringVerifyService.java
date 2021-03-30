package com.szmsd.delivery.service.wrapper;

import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.http.dto.ChargeWrapper;
import com.szmsd.http.dto.ProblemDetails;
import com.szmsd.http.dto.ResponseObject;

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
     * @param id id
     * @return int
     */
    int bringVerify(Long id);

    /**
     * 初始化
     *
     * @param delOutbound delOutbound
     * @return DelOutboundWrapperContext
     */
    DelOutboundWrapperContext initContext(DelOutbound delOutbound);

    /**
     * 计算包裹费用
     *
     * @param delOutboundWrapperContext delOutboundWrapperContext
     * @return ResponseObject<ChargeWrapper, ProblemDetails>
     */
    ResponseObject<ChargeWrapper, ProblemDetails> pricing(DelOutboundWrapperContext delOutboundWrapperContext);

    /**
     * 创建承运商物流订单（客户端）
     *
     * @param delOutboundWrapperContext delOutboundWrapperContext
     * @return String
     */
    String shipmentOrder(DelOutboundWrapperContext delOutboundWrapperContext);

    /**
     * 创建出库单
     *
     * @param delOutboundWrapperContext delOutboundWrapperContext
     * @param trackingNo                trackingNo
     * @return String
     */
    String shipmentCreate(DelOutboundWrapperContext delOutboundWrapperContext, String trackingNo);
}
