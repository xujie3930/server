package com.szmsd.bas.dto;

import com.szmsd.common.core.web.controller.QueryDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "BasWarehouseQueryDTO", description = "仓库列表查询字段")
public class BasWarehouseQueryDTO extends QueryDto {

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "仓库中文名")
    private String warehouseNameCn;

    @ApiModelProperty(value = "国家 - 代码")
    private String countryCode;

    @ApiModelProperty(value = "省")
    private String province;

    @ApiModelProperty(value = "城市")
    private String city;

    @ApiModelProperty(value = "状态：0无效，1有效")
    private String status;

}
