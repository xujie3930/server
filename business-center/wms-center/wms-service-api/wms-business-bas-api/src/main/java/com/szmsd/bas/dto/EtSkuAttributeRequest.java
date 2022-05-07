package com.szmsd.bas.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class EtSkuAttributeRequest {
    @ApiModelProperty(value = "操作人姓名")
    private String operator;

    @ApiModelProperty(value = "操作时间")
    private String operateOn;

    @ApiModelProperty(value = "目的仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "业务主键，用来做幂等校验")
    @NotBlank(message = "transactionId不能为空")
    private String transactionId;

    @ApiModelProperty(value = "产品编码")
    @NotBlank(message = "sku代码不能为空")
    private String sku;

    @ApiModelProperty(value = "SKU属性")
    @NotNull(message = "skuAttribute不能为空")
    private String skuAttribute;

}
