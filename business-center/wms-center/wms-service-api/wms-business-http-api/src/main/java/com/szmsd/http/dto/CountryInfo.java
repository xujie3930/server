package com.szmsd.http.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
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
