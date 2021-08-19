package com.szmsd.doc.api.warehouse.resp;

import com.szmsd.common.core.web.controller.QueryDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@Data
@Accessors(chain = true)
@ApiModel(value = "BasWarehouseQueryReq", description = "仓库列表查询条件")
public class BasWarehouseQueryReq extends QueryDto {
    @Size(max = 30)
    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;
    @Size(max = 200)
    @ApiModelProperty(value = "仓库中文名")
    private String warehouseNameCn;
    @Size(max = 30)
    @ApiModelProperty(value = "国家 - 代码")
    private String countryCode;
    @Size(max = 200)
    @ApiModelProperty(value = "省")
    private String province;
    @Size(max = 200)
    @ApiModelProperty(value = "城市")
    private String city;
    @Size(max = 10)
    @ApiModelProperty(value = "状态：0无效，1有效")
    private String status;

}