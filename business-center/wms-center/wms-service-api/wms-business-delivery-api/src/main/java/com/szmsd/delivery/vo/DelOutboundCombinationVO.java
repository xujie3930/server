package com.szmsd.delivery.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-07-02 15:11
 */
@Data
@ApiModel(value = "DelOutboundCombinationVO", description = "DelOutboundCombinationVO对象")
public class DelOutboundCombinationVO implements Serializable {

    @ApiModelProperty(value = "SKU")
    private String sku;

    @ApiModelProperty(value = "数量")
    private Long qty;
}
