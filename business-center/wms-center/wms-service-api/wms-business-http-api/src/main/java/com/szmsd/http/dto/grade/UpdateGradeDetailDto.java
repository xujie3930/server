package com.szmsd.http.dto.grade;

import com.szmsd.http.vo.Operation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "CustomGradeMainDto", description = "修改等级方案关联产品")
public class UpdateGradeDetailDto {

    @ApiModelProperty("关联产品集合")
    private List<GradeDto> pricingGradeRules;

    @ApiModelProperty("操作人")
    private String operatorName;

    @ApiModelProperty("操作人信息")
    private Operation userIdentity;

    @ApiModelProperty("模板id")
    private String templateId;

}
