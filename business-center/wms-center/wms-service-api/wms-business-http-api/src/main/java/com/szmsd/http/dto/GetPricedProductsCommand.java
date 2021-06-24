package com.szmsd.http.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "GetPricedProductsCommand")
public class GetPricedProductsCommand {

    @ApiModelProperty(value = "包裹信息")
    List<PackageInfo> packageInfos;
    @ApiModelProperty(value = "客户代码")
    private String clientCode;
    @ApiModelProperty(value = "发货类型---普货，电池，粉末等")
    private String shipmentType;
    @ApiModelProperty
    private Address fromAddress;

    @ApiModelProperty
    private Address toAddress;

    @ApiModelProperty(value = "联系信息")
    private ContactInfo toContactInfo;

    @ApiModelProperty(value = "标签")
    private List<String> tags;

    @ApiModelProperty(value = "等级")
    private String grade;

    @ApiModelProperty(value = "报价表")
    private String sheetCode;

    @ApiModelProperty(value = "参考号")
    private String refNo;

    @ApiModelProperty(value = "验证地址")
    private Boolean addressValifition;

    @ApiModelProperty(value = "计价时间，用于选择对应生效的折扣")
    private String calcTimeForDiscount;

    @ApiModelProperty(value = "需要忽略计算的费用类型")
    private List<String> ignoreChargeTypes;

    @ApiModelProperty(value = "增值税号")
    private String ioss;

}