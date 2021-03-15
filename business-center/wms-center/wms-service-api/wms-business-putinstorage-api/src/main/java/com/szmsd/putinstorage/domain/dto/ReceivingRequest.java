package com.szmsd.putinstorage.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "ReceivingRequest", description = "ReceivingRequest接收入库上架")
public class ReceivingRequest {

    @ApiModelProperty(value = "操作人姓名")
    private String operator;

    @ApiModelProperty(value = "操作时间")
    private String operateOn;

    @ApiModelProperty(value = "仓库代码 - 从入库单号中获取仓库", hidden = true)
    private String warehouseCode;

    @ApiModelProperty(value = "单号 - 入库单号")
    private String orderNo;

    @ApiModelProperty(value = "SKU")
    private String sku;

    @ApiModelProperty(value = "上架数量")
    private Integer qty;

}
