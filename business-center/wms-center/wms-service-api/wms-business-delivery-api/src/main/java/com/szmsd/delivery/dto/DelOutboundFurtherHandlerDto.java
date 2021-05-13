package com.szmsd.delivery.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-03-25 14:42
 */
@Data
@ApiModel(value = "DelOutboundFurtherHandlerDto", description = "DelOutboundFurtherHandlerDto对象")
public class DelOutboundFurtherHandlerDto implements Serializable {

    @NotEmpty(message = "订单号不能为空")
    @ApiModelProperty(value = "订单号")
    private String orderNo;
}
