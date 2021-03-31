package com.szmsd.inventory.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CountingRequestDto {

    @ApiModelProperty(value = "操作人姓名")
    private String operator;

    @ApiModelProperty(value = "操作时间")
    private String operateOn;

    @ApiModelProperty(value = "仓库")
    private String warehouseCode;

    @ApiModelProperty(value = "业务主键，用来做幂等校验")
    @NotBlank(message = "业务主键不能为空")
    private String transactionId;

    @ApiModelProperty(value = "SKU")
    @NotBlank(message = "SKU不能为空")
    private String sku;

    @ApiModelProperty(value = "所属盘点单号")
    private String orderNo;

    @NotNull
    @ApiModelProperty(value = "盘点时系统数量")
    private Integer systemQty;

    @NotNull
    @ApiModelProperty(value = "当时盘点数量")
    private Integer countingQty;

    @NotNull
    @ApiModelProperty(value = "差异数量，等于盘点数量减去系统数量")
    private Integer diffQty;

    @ApiModelProperty(value = "备注")
    private String remark;

}
