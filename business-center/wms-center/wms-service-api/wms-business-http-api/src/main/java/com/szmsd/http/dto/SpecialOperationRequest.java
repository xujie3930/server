package com.szmsd.http.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class SpecialOperationRequest implements Serializable {

    @ApiModelProperty(value = "操作类型")
    private String operationType;

    @ApiModelProperty(value = "操作类型描述")
    private String operationTypeDesc;

    @ApiModelProperty(value = "计费单位")
    private String unit;

    @ApiModelProperty(value = "备注")
    private String remark;

}
