package com.szmsd.bas.dto;


import com.szmsd.common.core.web.controller.QueryDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
@ApiModel(value = "", description = "BasTransportConfigDTO运输方式对象")
public class BasTransportConfigDTO extends QueryDto {

    @ApiModelProperty(value = "运输方式code")
    private String transportCode;

    @ApiModelProperty(value = "运输方式中文名称")
    private String transportName;

    @ApiModelProperty(value = "运输方式英文名称")
    private String transportNameEn;

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "发货服务code")
    private String productCode;

    @ApiModelProperty(value = "产品名称")
    private String productName;



}