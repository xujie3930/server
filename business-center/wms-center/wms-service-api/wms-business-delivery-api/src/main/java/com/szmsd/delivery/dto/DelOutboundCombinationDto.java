package com.szmsd.delivery.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-07-02 15:11
 */
@Data
@ApiModel(value = "DelOutboundCombinationDto", description = "DelOutboundCombinationDto对象")
public class DelOutboundCombinationDto implements Serializable {

    @ApiModelProperty(value = "SKU")
    private String sku;

    @ApiModelProperty(value = "数量")
    private Long qty;
}
