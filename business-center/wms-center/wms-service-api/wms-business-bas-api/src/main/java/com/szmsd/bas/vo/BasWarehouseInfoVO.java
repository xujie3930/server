package com.szmsd.bas.vo;

import com.szmsd.bas.domain.BasWarehouseCus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "BasWarehouseInfoVO", description = "仓库详情 ")
public class BasWarehouseInfoVO {

    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "目的仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "仓库英文名")
    private String warehouseNameEn;

    @ApiModelProperty(value = "仓库中文名")
    private String warehouseNameCn;

    @ApiModelProperty(value = "国家代码")
    private String countryCode;

    @ApiModelProperty(value = "国家名称")
    private String countryName;

    @ApiModelProperty(value = "国家中文名")
    private String countryChineseName;

    @ApiModelProperty(value = "国家显示名称")
    private String countryDisplayName;

    @ApiModelProperty(value = "省")
    private String province;

    @ApiModelProperty(value = "城市")
    private String city;

    @ApiModelProperty(value = "街道1")
    private String street1;

    @ApiModelProperty(value = "街道2")
    private String street2;

    @ApiModelProperty(value = "街道3")
    private String postcode;

    @ApiModelProperty(value = "是否需要VAT：0不需要，1需要")
    private String isCheckVat;

    @ApiModelProperty(value = "电话")
    private String telephone;

    @ApiModelProperty(value = "时区")
    private Integer timeZone;

    @ApiModelProperty(value = "联系人")
    private String contact;

    @ApiModelProperty(value = "状态：0无效，1有效")
    private String status;

    @ApiModelProperty(value = "白名单")
    private List<BasWarehouseCus> whiteCusList;

    @ApiModelProperty(value = "黑名单")
    private List<BasWarehouseCus> blackCusList;

}
