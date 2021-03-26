package com.szmsd.http.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "RemoteAreaTemplateSearchCriteria", description = "模板查询")
public class RemoteAreaTemplateSearchCriteria extends PageDTO {

    @ApiModelProperty("模板名称")
    private String name;

}
