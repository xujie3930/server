package com.szmsd.inventory.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class InventoryCheckQueryDTO {

    @ApiModelProperty(value = "申请单号")
    private List<String> orderNoList;

    @ApiModelProperty(value = "客户代码")
    private String customCode;

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "创建时间开始")
    private String createTimeStart;

    @ApiModelProperty(value = "创建时间结束")
    private String createTimeEnd;

}
