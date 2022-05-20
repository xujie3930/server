package com.szmsd.ec.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class TransferCallbackDTO {

    @ApiModelProperty(value = "电商单号")
    @NotBlank(message = "电商单号不能为空")
    private String orderNo;

    @ApiModelProperty("物流单号")
    private String transferNumber;

    @NotBlank(message = "承运商不能为空")
    @ApiModelProperty("承运商")
    private String logisticsRouteId;

    /**
     * 发货异常信息  如果发货失败 填入失败原因
     */
    private String transferErrorMsg;

}
