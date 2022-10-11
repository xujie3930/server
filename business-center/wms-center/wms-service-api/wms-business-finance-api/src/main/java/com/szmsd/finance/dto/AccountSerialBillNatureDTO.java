package com.szmsd.finance.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class AccountSerialBillNatureDTO implements Serializable {

    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "单号")
    private String no;

    @ApiModelProperty(value = "出库单订单类型")
    private String orderType;

    @ApiModelProperty(value = "费用类别")
    private String chargeCategory;

    @ApiModelProperty(value = "费用类型")
    private String chargeType;

    @ApiModelProperty(value = "业务类别")
    private String businessCategory;
}
