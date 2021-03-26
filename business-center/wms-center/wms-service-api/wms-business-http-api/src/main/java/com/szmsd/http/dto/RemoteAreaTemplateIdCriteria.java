package com.szmsd.http.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "RemoteAreaTemplateIdCriteria", description = "模板id查询器")
public class RemoteAreaTemplateIdCriteria {

    @ApiModelProperty("模板id")
    private String templateId;

}
