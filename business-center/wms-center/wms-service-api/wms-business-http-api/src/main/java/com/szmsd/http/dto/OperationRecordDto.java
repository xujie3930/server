package com.szmsd.http.dto;

import com.szmsd.http.dto.UserIdentity;
import com.szmsd.http.vo.DateOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "UpdateCustOperationRecordDtoomMainDto", description = "操作记录")
public class OperationRecordDto {

    @ApiModelProperty("操作日志记录对象类型")
    private String objectType;

    @ApiModelProperty("操作日志记录的对象的Id")
    private String objectId;

    @ApiModelProperty("具体的操作记录详情")
    private List<OperationRecordDetailDto> recordItems;

    @ApiModelProperty("操作人信息")
    private DateOperation lastModifyOperation;




}
