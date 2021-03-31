package com.szmsd.inventory.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class InventoryCheckDTO {

    @ApiModelProperty(value = "客户代码")
    private String customCode;

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "申请单号")
    private String orderNo;

    @ApiModelProperty(value = "sku")
    private String sku;

    @ApiModelProperty(value = "系统数量")
    private Integer systemQty;

    @ApiModelProperty(value = "盘点数量")
    private Integer countingQty;

    @ApiModelProperty(value = "差异数量，等于盘点数量减去系统数量")
    private Integer diffQty;

    @ApiModelProperty(value = "完成时间")
    private String checkTime;

    @ApiModelProperty(value = "备注")
    private String remark;

}
