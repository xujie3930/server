package com.szmsd.http.api.service;

import com.szmsd.common.core.utils.FileStream;
import com.szmsd.http.dto.*;

/**
 * @author zhangyuyuan
 * @date 2021-03-30 11:51
 */
public interface IHtpCarrierClientService {

    /**
     * 创建承运商物流订单（客户端）
     *
     * @param command command
     * @return ResponseObject<ShipmentOrderResult, ProblemDetails>
     */
    ResponseObject.ResponseObjectWrapper<ShipmentOrderResult, ProblemDetails> shipmentOrder(CreateShipmentOrderCommand command);

    /**
     * 取消承运商物流订单（客户端）
     *
     * @param command command
     * @return ResponseObject.ResponseObjectWrapper<CancelShipmentOrderBatchResult, ErrorDataDto>
     */
    ResponseObject.ResponseObjectWrapper<CancelShipmentOrderBatchResult, ErrorDataDto> cancellation(CancelShipmentOrderCommand command);

    /**
     * 根据订单号返回标签文件流
     *
     * @param command command
     * @return ResponseObject.ResponseObjectWrapper<FileStream, ProblemDetails>
     */
    ResponseObject.ResponseObjectWrapper<FileStream, ProblemDetails> label(CreateShipmentOrderCommand command);
}
