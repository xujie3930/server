package com.szmsd.http.dto.discount;

import com.szmsd.http.vo.Operation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "CustomGradeMainDto", description = "修改等级方案关联产品")
public class UpdateDiscountDetailDto {

    @ApiModelProperty("关联产品集合")
    private List<DiscountDetailDto> pricingDiscountRules;

    @ApiModelProperty("操作人")
    private String operatorName;

    @ApiModelProperty("操作人信息")
    private Operation userIdentity;

    @ApiModelProperty("模板id")
    private String templateId;

}
