package com.szmsd.http.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "PricedExemption", description = "报价免收的配置项（既满足一定条件下，免收某种费用）")
public class PricedExemption {

    @ApiModelProperty("费用名称")
    private String chargeType;

    @ApiModelProperty("费用免收规则的类型")
    private String pricingExemptionType;

    @ApiModelProperty("客户代码")
    private String clientCode;

    @ApiModelProperty("国家")
    private String country;

    @ApiModelProperty("城市过滤")
    private String city;

    @ApiModelProperty("省份过滤")
    private String province;

    @ApiModelProperty("街道过滤")
    private String street;

    @ApiModelProperty("收货人过滤")
    private String recipient;

    @ApiModelProperty("邮编过滤")
    private String postCode;

}
