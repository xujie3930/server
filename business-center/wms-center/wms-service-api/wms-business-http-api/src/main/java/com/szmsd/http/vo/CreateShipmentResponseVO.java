package com.szmsd.http.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zhangyuyuan
 * @date 2021-03-09 13:55
 */
@ApiModel(value = "CreateShipmentResponseVO", description = "CreateShipmentResponseVO对象")
public class CreateShipmentResponseVO extends ResponseVO {

    @ApiModelProperty(value = "创建的单号")
    private String orderNo;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }
}
