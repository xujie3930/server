package com.szmsd.chargerules.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "BasProductServiceDao", description = "产品服务传参类")
public class BasProductServiceDao {

    @ApiModelProperty(value = "产品代码")
    private String productCode;

    @ApiModelProperty(value = "客户代码")
    private String customCode;
}
