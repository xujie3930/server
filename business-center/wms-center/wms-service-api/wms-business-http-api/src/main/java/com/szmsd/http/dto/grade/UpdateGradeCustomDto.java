package com.szmsd.http.dto.grade;

import com.szmsd.http.dto.custom.AssociatedCustomersDto;
import com.szmsd.http.vo.Operation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "CustomGradeMainDto", description = "修改等级方案关联客户")
public class UpdateGradeCustomDto {

    @ApiModelProperty("关联客户集合")
    private List<AssociatedCustomersDto> associatedCustomers;

    @ApiModelProperty("操作人")
    private String operatorName;

    @ApiModelProperty("操作人信息")
    private Operation userIdentity;

    @ApiModelProperty("模板id")
    private String templateId;

}
