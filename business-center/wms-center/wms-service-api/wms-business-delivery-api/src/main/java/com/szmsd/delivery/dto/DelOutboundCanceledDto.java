package com.szmsd.delivery.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-25 14:42
 */
@Data
@ApiModel(value = "DelOutboundCanceledDto", description = "DelOutboundCanceledDto对象")
public class DelOutboundCanceledDto implements Serializable {

    @ApiModelProperty(value = "IDS")
    private List<Long> ids;

    @ApiModelProperty(value = "订单编号")
    private List<String> orderNos;
}
