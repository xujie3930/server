package com.szmsd.putinstorage.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "InboundReceiptDTO", description = "入库参数")
public class InboundReceiptReviewDTO {

    @ApiModelProperty(value = "入库单号")
    private String warehouseNo;

    @ApiModelProperty(value = "状态3审核通过，-3审核失败")
    private String status;

    @ApiModelProperty(value = "审核备注")
    private String reviewRemark;

}
