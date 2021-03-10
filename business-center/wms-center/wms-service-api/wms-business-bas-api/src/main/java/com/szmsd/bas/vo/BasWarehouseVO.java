package com.szmsd.bas.vo;

import com.szmsd.common.core.language.annotation.FieldJsonI18n;
import com.szmsd.common.core.language.constant.RedisLanguageTable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "BasWarehouseVO", description = "仓库列表")
public class BasWarehouseVO {

    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "仓库名称 - 当前系统语言")
    @FieldJsonI18n(type = RedisLanguageTable.BAS_WAREHOUSE)
    private String warehouseName;

    @ApiModelProperty(value = "国家")
    private String countryName;

    @ApiModelProperty(value = "城市")
    private String city;

    @ApiModelProperty(value = "时区")
    private Integer timeZone;

    @ApiModelProperty(value = "地址")
    private Integer address;

    @ApiModelProperty(value = "VAT")
    private String isCheckVat;

    @ApiModelProperty(value = "状态：0无效，1有效")
    private String status;

}
