package com.szmsd.inventory.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "InventoryInspectionQueryDTO", description = "InventoryInspectionQueryDTO验货")
public class InventoryInspectionQueryDTO {

    @ApiModelProperty(value = "申请单号")
    private List<String> inspectionNoList;

    @ApiModelProperty(value = "客户代码")
    private String customCode;

    @ApiModelProperty(value = "审核状态")
    private Integer status;

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "创建时间开始")
    private String createTimeStart;

    @ApiModelProperty(value = "创建时间结束")
    private String createTimeEnd;



}
