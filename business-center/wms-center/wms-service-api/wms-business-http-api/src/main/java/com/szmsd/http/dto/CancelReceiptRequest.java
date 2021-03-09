package com.szmsd.http.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 取消入库单
 */
@Data
@Accessors(chain = true)
public class CancelReceiptRequest {

    /** 仓库代码 **/
    private String warehouseCode;

    /** 订单号 **/
    private String orderNo;

}
