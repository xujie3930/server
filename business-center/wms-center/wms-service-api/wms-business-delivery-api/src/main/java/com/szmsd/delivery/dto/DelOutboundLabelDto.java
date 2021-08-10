package com.szmsd.delivery.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-05 14:21
 */
@Data
@ApiModel(value = "DelOutboundLabelDto", description = "DelOutboundLabelDto对象")
public class DelOutboundLabelDto implements Serializable {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "订单号")
    private List<String> orderNos;
}
