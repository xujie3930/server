package com.szmsd.putinstorage.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "ReceiptRequest", description = "ReceiptRequest转运收货调用")
public class ReceiptRequest {

    @ApiModelProperty(value = "操作时间")
    private String operateOn;

    @ApiModelProperty(value = "入库单号")
    private String orderNo;

    @ApiModelProperty(value = "包裹出库单号")
    private String packageOrderNo;

    @ApiModelProperty(value = "仓库编号", hidden = true)
    private String warehouseCode;

}
