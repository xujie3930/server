package com.szmsd.bas.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Data
@Accessors(chain = true)
@ApiModel(value = "AddWarehouseRequest", description = "创建/更新仓库")
public class AddWarehouseRequest {

    @ApiModelProperty(value = "操作人姓名")
    private String operator;

    @ApiModelProperty(value = "操作时间")
    private String operateOn;

    @NotBlank(message = "仓库代码不能为空")
    @ApiModelProperty(value = "目的仓库编码")
    private String warehouseCode;

    @NotBlank(message = "仓库英文名不能为空")
    @ApiModelProperty(value = "仓库英文名")
    private String warehouseNameEn;

    @NotBlank(message = "仓库中文名不能为空")
    @ApiModelProperty(value = "仓库中文名")
    private String warehouseNameCn;

    @NotBlank(message = "是否需要VAT不能为空")
    @ApiModelProperty(value = "是否需要VAT：0不需要，1需要")
    private String isCheckVat;

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

    @ApiModelProperty(value = "电话")
    private String telephone;

    @ApiModelProperty(value = "时区")
    private Integer timeZone;

    @ApiModelProperty(value = "联系人")
    private String contact;

    @ApiModelProperty(value = "状态：0无效，1有效", hidden = true)
    private String status;

}
