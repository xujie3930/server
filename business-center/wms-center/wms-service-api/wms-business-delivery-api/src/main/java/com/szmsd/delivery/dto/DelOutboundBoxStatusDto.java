package com.szmsd.delivery.dto;

import com.szmsd.common.core.utils.FileStream;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author zhouyonglai
 * @date 2022-07-19 18:20
 */
@Data
@ApiModel(value = "DelOutboundReceiveLabelDto", description = "DelOutboundReceiveLabelDto对象")
public class DelOutboundBoxStatusDto implements Serializable {

    @ApiModelProperty(value = "订单号")
    @NotEmpty(message = "订单号不能为空")
    private String orderNo;

    @ApiModelProperty(value = "操作类型")
    @NotEmpty(message = "操作类型不能为空")
    private String operationType;

    @ApiModelProperty(value = "箱号")
    @NotEmpty(message = "箱号不能为空")
    private String boxNo;



}
