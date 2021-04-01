package com.szmsd.inventory.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class InventoryCheckDTO {

    @ApiModelProperty(value = "客户代码")
    private String customCode;

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "完成时间")
    private String checkTime;

    @ApiModelProperty(value = "详情")
    private List<InventoryCheckDetailsDTO> inventoryCheckDetailsList;

    @ApiModelProperty(value = "备注")
    private String remark;

}
