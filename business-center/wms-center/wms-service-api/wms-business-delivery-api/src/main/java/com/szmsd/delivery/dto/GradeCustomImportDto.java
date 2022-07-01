package com.szmsd.delivery.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "GradeCustomImportDto", description = "等级方案-导入关联客户")
public class GradeCustomImportDto {


    @ApiModelProperty("客户编号")
    private String clientCode;


    @ApiModelProperty("有效开始时间")
    private String beginTime;

    @ApiModelProperty("有效解速时间")
    private String endTime;

    @ApiModelProperty("是否有效")
    private Boolean isValid;























}
