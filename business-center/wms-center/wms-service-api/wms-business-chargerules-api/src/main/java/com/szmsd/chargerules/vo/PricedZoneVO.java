package com.szmsd.chargerules.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "PricedZoneVO", description = "分区表")
public class PricedZoneVO {

    @ApiModelProperty("分区名称")
    private String name;

    @ApiModelProperty("起始国家 - 发出地国家")
    private String fromCountry;

    @ApiModelProperty("起始邮编 - 发出地邮编规则，如（*, 100-102）")
    private String fromPostCode;

    @ApiModelProperty("起始城市 - 发出地城市")
    private String fromCity;

    @ApiModelProperty("起始州/省份 - 发出地州/省")
    private String fromProvince;

    @ApiModelProperty("目的国家 - 国家")
    private String country;
    @ApiModelProperty("目的邮编 - 邮编")
    private String postCode;

    @ApiModelProperty("目的城市 - 城市")
    private String city;

    @ApiModelProperty("目的州/省份 - 州/省")
    private String province;

    @ApiModelProperty("物流节点 - 节点")
    private String node;

}
