package com.szmsd.http.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CountryInfo {

    @ApiModelProperty
    private String alpha2Code;

    @ApiModelProperty
    private String alpha3Code;

    @ApiModelProperty
    private String enName;

    @ApiModelProperty
    private String cnName;

}
