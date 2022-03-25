package com.szmsd.http.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "Address")
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @ApiModelProperty
    private String street1;

    @ApiModelProperty
    private String street2;

    @ApiModelProperty
    private String street3;

    @ApiModelProperty
    private String postCode;

    @ApiModelProperty
    private String city;

    @ApiModelProperty
    private String province;

    @ApiModelProperty
    private CountryInfo country;

}
