package com.szmsd.putinstorage.domain.remote.request;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 收货单明细
 */
@Data
@Accessors(chain = true)
public class ReceiptDetailInfo {

    /** SKU **/
    private String sku;

    /** 数量 **/
    private Integer qty;

    /** 原编码（入库货物上面的编码，非SKU编码） **/
    private String originCode;

}
