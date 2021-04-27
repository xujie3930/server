package com.szmsd.chargerules.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "OperationDTO", description = "OperationDTO对象")
public class OperationDTO implements Serializable {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "仓库")
    private String warehouseCode;

    @ApiModelProperty(value = "操作类型")
    private String operationType;

    @ApiModelProperty(value = "订单类型")
    private String orderType;

    @ApiModelProperty(value = "首件价格")
    private BigDecimal firstPrice;

    @ApiModelProperty(value = "续件价格")
    private BigDecimal nextPrice;

    @ApiModelProperty(value = "重量")
    private Double weight;

    @ApiModelProperty(value = "最小重量 - 开始 单位: g 大于")
    private Double minimumWeight;

    @ApiModelProperty(value = "最大重量 - 结束 单位: g 小于等于")
    private Double maximumWeight;

    @ApiModelProperty(value = "计费单位")
    private String unit;

    @ApiModelProperty(value = "备注")
    private String remark;

    public OperationDTO(String operationType, String orderType, String warehouseCode, Double weight) {
        this.operationType = operationType;
        this.orderType = orderType;
        this.warehouseCode = warehouseCode;
        this.weight = weight;
    }
}
