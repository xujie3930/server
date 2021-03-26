package com.szmsd.http.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "RemoteAreaTemplate", description = "偏远地区报价模板")
public class RemoteAreaTemplate {

    @ApiModelProperty
    private String id;

    @ApiModelProperty
    private String name;

    @ApiModelProperty
    private List<RemoteAreaRule> remoteAreaRules;

}
