package com.szmsd.putinstorage.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;


@Data
@Accessors(chain = true)
@ApiModel(value = "ReceiptRequest", description = "ReceiptRequest转运收货调用")
public class ReceiptRequest {

    @NotBlank(message = "操作时间")
    @ApiModelProperty(value = "操作时间,字符串")
    private String operateOn;

    @NotBlank(message = "入库单号")
    @ApiModelProperty(value = "入库单号")
    private String orderNo;

    @NotBlank(message = "包裹出库单号")
    @ApiModelProperty(value = "包裹出库单号")
    private String packageOrderNo;

    @ApiModelProperty(value = "仓库编号", hidden = true)
    private String warehouseCode;

}
