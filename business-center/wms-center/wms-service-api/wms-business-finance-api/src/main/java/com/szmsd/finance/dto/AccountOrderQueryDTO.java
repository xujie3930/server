package com.szmsd.finance.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AccountOrderQueryDTO implements Serializable {

    @ApiModelProperty(value = "订单号")
    private List<String> orderNoList;

    @ApiModelProperty(value = "PRC费用")
    private Integer prcState;

}
