package com.szmsd.http.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class TagSurchargeRequest {

    @ApiModelProperty(value = "最终计价的产品代码")
    private String finalPricingProductCode;

    @ApiModelProperty(value = "计费重")
    private Weight calcWeight;

    @ApiModelProperty(value = "实重")
    private Weight actualWeight;

    @ApiModelProperty(value = "")
    private Packing packing;

    @ApiModelProperty(value = "分区")
    private String zoneName;

    @ApiModelProperty(value = "仓库")
    private String wareHouse;

    @ApiModelProperty(value = "产品代码")
    private String productCode;

    @ApiModelProperty(value = "标签")
    private List<String> tags;

    @ApiModelProperty(value = "客户代码")
    private String clientCode;

    @ApiModelProperty(value = "报价表代码")
    private String sheetCode;

    @ApiModelProperty(value = "计费等级")
    private String grade;

    private UserIdentity userIdentity;


}
