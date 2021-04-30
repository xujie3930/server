package com.szmsd.inventory.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "InventoryInspectionDTO", description = "InventoryInspectionDTO验货")
public class InventoryInspectionDTO {

    @ApiModelProperty(value = "主键ID")
    private Integer id;

    @ApiModelProperty(value = "申请单号")
    private String inspectionNo;

    @ApiModelProperty(value = "客户代码")
    private String customCode;

    @ApiModelProperty(value = "客户名称")
    private String customName;

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "仓库名称")
    private String warehouseName;

    @ApiModelProperty(value = "库存盘点审批结果")
    private Integer status;

    @ApiModelProperty(value = "审批不通过原因")
    private String reason;

    @ApiModelProperty(value = "备注")
    private String remark;

}
