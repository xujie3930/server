package com.szmsd.http.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 创建入库单请求
 */
@Data
@Accessors(chain = true)
public class CreateReceiptRequest implements Serializable {

    /** 仓库代码 **/
    private String warehouseCode;

    /** 入库单类型 **/
    private String orderType;

    /** 卖家代码 **/
    private String sellerCode;

    /** 挂号 **/
    private String trackingNumber;

    /** 下单备注 **/
    private String remark;

    /** 参照单号（传OMS单号） **/
    private String refOrderNo;

    /** 收货单明细 **/
    private List<ReceiptDetailInfo> details;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
