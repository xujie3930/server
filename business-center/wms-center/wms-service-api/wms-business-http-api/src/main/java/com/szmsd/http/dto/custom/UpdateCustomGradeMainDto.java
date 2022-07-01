package com.szmsd.http.dto.custom;

import com.szmsd.http.dto.UserIdentity;
import com.szmsd.http.vo.Operation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "CustomGradeMainDto", description = "客户方案-修改等级方案")
public class UpdateCustomGradeMainDto {

    @ApiModelProperty("产品等级")
    private String templateId;

    @ApiModelProperty("有效起始时间")
    private String beginTime;

    @ApiModelProperty("有效结束时间 若时间为DateTime.MaxValue 表示无结束时间")
    private String endTime;

    @ApiModelProperty("操作人")
    private String operatorName;

    @ApiModelProperty("修改人")
    private UserIdentity userIdentity;


    @ApiModelProperty("客户编号")
    private String clientCode;


}
