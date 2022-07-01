package com.szmsd.http.dto.custom;

import com.szmsd.http.dto.grade.GradeDto;
import com.szmsd.http.vo.Operation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "CustomGradeMainDto", description = "客户方案-修改折扣方案规则")
public class CustomGradeMainDto {

    @ApiModelProperty("产品等级")
    private List<GradeDto> pricingGradeRules;

    @ApiModelProperty("操作人")
    private String operatorName;

    @ApiModelProperty("操作人信息")
    private Operation userIdentity;


    @ApiModelProperty("客户编号")
    private String clientCode;

}
