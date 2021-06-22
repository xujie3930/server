package com.szmsd.inventory.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "InventoryWarningQueryDTO", description = "库存对账 - 查询入参")
public class InventoryWarningQueryDTO {

    @ApiModelProperty(value = "批次号")
    private String batchNo;

    @ApiModelProperty(value = "仓库代码")
    private String warehouse;

    @ApiModelProperty(value = "SKU")
    private String sku;

    @ApiModelProperty(value = "客户代码")
    private String cusCode;

    @ApiModelProperty(value = "开始时间 - 由接口调用方定义")
    private String startTime;

    @ApiModelProperty(value = "结束时间 - 由接口调用方定义")
    private String endTime;
}
