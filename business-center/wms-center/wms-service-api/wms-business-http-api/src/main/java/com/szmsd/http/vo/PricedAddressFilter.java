package com.szmsd.http.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "PricedAddressFilter", description = "地址过滤")
public class PricedAddressFilter {

    @ApiModelProperty
    private String addressFilterType;

    @ApiModelProperty("物流节点")
    private String toNodeFilter;

    @ApiModelProperty("国家")
    private String countryFilter;

    @ApiModelProperty("城市")
    private String cityFilter;

    @ApiModelProperty("省")
    private String provinceFilter;

    @ApiModelProperty("地址")
    private String streetFilter;

    @ApiModelProperty("收件人")
    private String recipientFilter;

    @ApiModelProperty("邮编")
    private String postCodeFilter;

}
