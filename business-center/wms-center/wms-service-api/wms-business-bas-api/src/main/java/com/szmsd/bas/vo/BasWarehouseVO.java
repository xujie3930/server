package com.szmsd.bas.vo;

import com.szmsd.common.core.language.annotation.FieldJsonI18n;
import com.szmsd.common.core.language.constant.RedisLanguageTable;
import com.szmsd.common.core.language.enums.LocalLanguageTypeEnum;
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

    @ApiModelProperty(value = "国家代码")
    private String countryCode;

    @ApiModelProperty(value = "国家显示名称")
    private String countryDisplayName;

    @ApiModelProperty(value = "省")
    private String province;

    @ApiModelProperty(value = "城市")
    private String city;

    @ApiModelProperty(value = "时区")
    private Integer timeZone;

    @ApiModelProperty(value = "地址")
    private String address;

    @ApiModelProperty(value = "VAT")
    @FieldJsonI18n(localLanguageType = LocalLanguageTypeEnum.NEED)
    private String isCheckVat;

    @ApiModelProperty(value = "状态：0无效，1有效")
    private String status;

    @ApiModelProperty(value = "入库单是否人工审核：0自动审核，1人工审核")
    @FieldJsonI18n(localLanguageType = LocalLanguageTypeEnum.INBOUND_RECEIPT_REVIEW)
    private String inboundReceiptReview;

    @ApiModelProperty(value = "联系人")
    private String contact;

    @ApiModelProperty(value = "邮编")
    private String postcode;

    @ApiModelProperty(value = "电话")
    private String telephone;
}
