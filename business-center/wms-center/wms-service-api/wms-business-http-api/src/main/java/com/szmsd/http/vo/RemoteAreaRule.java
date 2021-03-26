package com.szmsd.http.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "RemoteAreaRule", description = "报价规则")
public class RemoteAreaRule {

    @ApiModelProperty(value = "国家")
    private String country;

    @ApiModelProperty(value = "省份")
    private String province;

    @ApiModelProperty(value = "城市")
    private String city;

    @ApiModelProperty(value = "邮编From")
    private String postCodeFrom;

    @ApiModelProperty(value = "邮编To")
    private String postCodeTo;

    @ApiModelProperty(value = "收件人")
    private String receiver;

}
