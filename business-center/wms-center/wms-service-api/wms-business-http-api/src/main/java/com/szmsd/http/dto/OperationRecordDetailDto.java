package com.szmsd.http.dto;

import com.szmsd.http.vo.DateOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "OperationRecordDetailDto", description = "操作记录明细")
public class OperationRecordDetailDto {

    @ApiModelProperty("操作的名称")
    private String operationName;

    @ApiModelProperty("操作详述")
    private String operationDetail;

    @ApiModelProperty("修改人信息")
    private DateOperation operation;




}
