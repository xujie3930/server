package com.szmsd.chargerules.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "PricedAddressFilterVO", description = "地址限制")
public class PricedAddressFilterVO {

    @ApiModelProperty("地址限制类型")
    private String addressFilterType;

    @ApiModelProperty("物流节点")
    private String toNodeFilter;

    @ApiModelProperty("收件人")
    private String recipientFilter;

    @ApiModelProperty("街道 - 地址")
    private String streetFilter;

    @ApiModelProperty("城市")
    private String cityFilter;

    @ApiModelProperty("省")
    private String provinceFilter;

    @ApiModelProperty("国家")
    private String countryFilter;

    @ApiModelProperty("邮编")
    private String postCodeFilter;
}
