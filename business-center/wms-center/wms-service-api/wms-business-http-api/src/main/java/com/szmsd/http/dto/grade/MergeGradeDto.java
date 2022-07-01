package com.szmsd.http.dto.grade;

import com.szmsd.http.dto.UserIdentity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "MergeGradeDto", description = "新增/修改等级")
public class MergeGradeDto {

    @ApiModelProperty("方案Id")
    private String id;

    @ApiModelProperty("方案名称")
    private String name;

    @ApiModelProperty("优先级，值越大，优先级别越高")
    private String order;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("有效开始时间")
    private String effectiveBeginTime;

    @ApiModelProperty("有效结束时间")
    private String effectiveEndTime;

    @ApiModelProperty("操作人信息")
    private UserIdentity userIdentity;





}
