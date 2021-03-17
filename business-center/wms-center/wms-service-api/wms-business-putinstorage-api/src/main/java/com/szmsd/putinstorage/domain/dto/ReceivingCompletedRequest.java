package com.szmsd.putinstorage.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "ReceivingCompletedRequest", description = "ReceivingCompletedRequest接收完成入库")
public class ReceivingCompletedRequest {

    @ApiModelProperty(value = "操作人姓名")
    private String operator;

    @ApiModelProperty(value = "操作时间")
    private String operateOn;

    @ApiModelProperty(value = "仓库代码 - 从入库单号中获取仓库", hidden = true)
    private String warehouseCode;

    @ApiModelProperty(value = "单号 - 入库单号")
    private String orderNo;

}
