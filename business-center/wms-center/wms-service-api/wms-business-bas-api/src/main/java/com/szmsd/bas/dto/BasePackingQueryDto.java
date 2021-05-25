package com.szmsd.bas.dto;

import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BasePackingQueryDto {
    @ApiModelProperty(value = "物料名称")
    @Excel(name = "物料名称")
    private String name;

    @ApiModelProperty(value = "物料编码")
    @Excel(name = "物料编码")
    private String code;

    @ApiModelProperty(value = "仓库编码")
    private String warehouseCode;
}
