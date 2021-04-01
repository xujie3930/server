package com.szmsd.inventory.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(value = "AdjustRequestDto", description = "AdjustRequestDto对象")
public class AdjustRequestDto {

    @ApiModelProperty(value = "操作人姓名")
    private String operator;

    @ApiModelProperty(value = "操作时间")
    private String operateOn;

    @ApiModelProperty(value = "仓库")
    @NotBlank(message = "仓库编号不能为空")
    private String warehouseCode;

    @ApiModelProperty(value = "业务主键，用来做幂等校验")
    @NotBlank(message = "业务主键不能为空")
    private String transactionId;

    @ApiModelProperty(value = "SKU")
    @NotBlank(message = "SKU不能为空")
    private String sku;

    @ApiModelProperty(value = "关联单号（非必填）")
    private String orderNo;

    @ApiModelProperty(value = "调整数量")
    @NotNull
    private Integer qty;

    @ApiModelProperty(value = "备注")
    private String remark;

}
