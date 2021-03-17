package com.szmsd.http.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "KeyValuePair", description = "KeyValuePair")
public class KeyValuePair {

    @ApiModelProperty(value = "KEY")
    private String key;

    @ApiModelProperty(value = "VALUE")
    private String value;

}
