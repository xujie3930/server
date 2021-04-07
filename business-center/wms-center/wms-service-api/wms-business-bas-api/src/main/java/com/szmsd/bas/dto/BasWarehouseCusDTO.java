package com.szmsd.bas.dto;

import com.szmsd.bas.domain.BasWarehouseCus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "BasWarehouseCusDTO", description = "仓库黑白名单")
public class BasWarehouseCusDTO {

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "黑白名单用户")
    List<BasWarehouseCus> warehouseCusList;

}
