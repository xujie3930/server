package com.szmsd.http.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "PricedZone", description = "报价表分区信息")
public class PricedZone {

    @ApiModelProperty("分区名称")
    private String name;

    @ApiModelProperty("国家")
    private String country;

    @ApiModelProperty("城市")
    private String city;

    @ApiModelProperty("州/省")
    private String province;

    @ApiModelProperty("邮编")
    private String postCode;

    @ApiModelProperty("节点")
    private String node;

    @ApiModelProperty("发出地国家")
    private String fromCountry;

    @ApiModelProperty("发出地城市")
    private String fromCity;

    @ApiModelProperty("发出地州/省")
    private String fromProvince;

    @ApiModelProperty("发出地邮编规则，如（*, 100-102）")
    private String fromPostCode;

}
