package com.szmsd.inventory.domain.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class InboundInventoryDTO {

    /** 操作人姓名 **/
    private String operator;

    /** 操作时间 **/
    private String operateOn;

    /** 仓库代码 - 沟通过说不需要 从入库单号中获取仓库 **/
    private String warehouseCode;

    /** 单号 - 入库单号 **/
    private String orderNo;

    /** SKU **/
    private String sku;

    /** 上架数量 **/
    private Integer qty;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
