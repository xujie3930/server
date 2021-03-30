package com.szmsd.chargerules.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value = "BasSpecialOperationDTO", description = "特殊操作")
public class BasSpecialOperationRequestDTO {

    @ApiModelProperty(value = "操作人姓名")
    private String operator;

    @ApiModelProperty(value = "操作时间")
    private String operateOn;

    @ApiModelProperty(value = "仓库")
    private String warehouseCode;

    @NotBlank(message = "业务主键不能为空")
    @ApiModelProperty(value = "业务主键，用来做幂等校验")
    private String transactionId;

    @NotBlank(message = "操作单号不能为空")
    @ApiModelProperty(value = "操作单号")
    private String operationOrderNo;

    @NotBlank(message = "订单号不能为空")
    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @NotBlank(message = "订单类型不能为空")
    @ApiModelProperty(value = "订单类型")
    private String orderType;

    @NotBlank(message = "操作类型不能为空")
    @ApiModelProperty(value = "操作类型")
    private String operationType;

    @Min(1)
    @ApiModelProperty(value = "数量")
    private Integer qty;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "计费单位")
    private String unit;

    @ApiModelProperty(value = "oms备注")
    private String omsRemark;

}
