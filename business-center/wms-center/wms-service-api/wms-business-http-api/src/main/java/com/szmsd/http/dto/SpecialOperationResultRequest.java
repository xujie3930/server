package com.szmsd.http.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class SpecialOperationResultRequest implements Serializable {

    @ApiModelProperty(value = "操作单号")
    private String operationOrderNo;

    @ApiModelProperty(value = "状态（审核结果）通过：Pass 驳回：Reject")
    private String status;

    @ApiModelProperty(value = "备注")
    private String remark;

}
