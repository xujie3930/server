package com.szmsd.http.dto;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class SpecialOperationResultRequest implements Serializable {

    // 仓库编码 - 非业务数据
    @JSONField(serialize = false)
    private String warehouseCode;

    @ApiModelProperty(value = "操作单号")
    private String operationOrderNo;

    @ApiModelProperty(value = "状态（审核结果）通过：Pass 驳回：Reject")
    private String status;

    @ApiModelProperty(value = "备注")
    private String remark;

}
