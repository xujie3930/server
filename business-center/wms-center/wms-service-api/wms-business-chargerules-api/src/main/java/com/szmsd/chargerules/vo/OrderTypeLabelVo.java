package com.szmsd.chargerules.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "OrderTypeLabelVo", description = "订单类型")
public class OrderTypeLabelVo {

    @ApiModelProperty("订单类型编码")
    private String code;

    @ApiModelProperty("订单类型名称")
    private String label;

}
