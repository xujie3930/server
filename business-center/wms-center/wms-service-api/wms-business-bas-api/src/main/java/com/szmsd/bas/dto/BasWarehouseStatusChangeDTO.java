package com.szmsd.bas.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "BasWarehouseStatusChangeDTO", description = "仓库状态变更")
public class BasWarehouseStatusChangeDTO {

    @ApiModelProperty(value = "目的仓库编码")
    private String warehouseCode;
    @ApiModelProperty(value = "状态：0无效，1有效")
    private String status;

}
