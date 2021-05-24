package com.szmsd.bas.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CreatePackingRequest {
    @ApiModelProperty(value = "操作人姓名")
    private String operator;

    @ApiModelProperty(value = "操作时间")
    private String operateOn;

    @ApiModelProperty(value = "仓库编码")
    @NotBlank(message = "仓库编码不能为空")
    private String warehouseCode;

    @ApiModelProperty(value = "包材类别")
    @NotBlank(message = "包材类别不能为空")
    private String packingMaterialType;

    @ApiModelProperty(value = "包材代码，例如B001")
    @NotBlank(message = "包材代码不能为空")
    private String packageMaterialCode;

    @ApiModelProperty(value = "包材名称")
    @NotBlank(message = "包材名称不能为空")
    private String packageMaterialName;

    @ApiModelProperty(value = "包材规格")
    @NotBlank(message = "包材规格不能为空")
    private String packageMaterialSize;

    @ApiModelProperty(value = "是否激活")
    @NotNull(message = "是否激活不能为空")
    private Boolean isActive;

    @ApiModelProperty(value = "备注")
    private String remark;
}
