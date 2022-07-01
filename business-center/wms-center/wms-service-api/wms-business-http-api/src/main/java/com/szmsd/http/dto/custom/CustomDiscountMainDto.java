package com.szmsd.http.dto.custom;

import com.szmsd.http.vo.Operation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "CustomDiscountMainDto", description = "客户方案-修改折扣方案规则")
public class CustomDiscountMainDto {

    @ApiModelProperty("客户编号")
    private String clientCode;

    @ApiModelProperty("pricingDiscountRules")
    private List<DiscountDto> pricingDiscountRules;

    @ApiModelProperty("操作人")
    private String operatorName;

    @ApiModelProperty("操作人信息")
    private Operation userIdentity;

}
