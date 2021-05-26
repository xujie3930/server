package com.szmsd.bas.dto;

import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BasePackingQueryDto {
    @ApiModelProperty(value = "编码")
    private String packageMaterialCode;

    @ApiModelProperty(value = "名称")
    private String packageMaterialName;

    @ApiModelProperty(value = "仓库编码")
    private String warehouseCode;
}
