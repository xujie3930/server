package com.szmsd.http.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 取消承运商物流订单
 *
 * @author zhangyuyuan
 * @date 2021-04-07 11:17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class CancelShipmentOrderCommand implements Serializable {

    @ApiModelProperty(value = "请求唯一标识，可以使用GUID")
    private String referenceNumber;

    @ApiModelProperty(value = "待取消订单号")
    private List<CancelShipmentOrder> cancelShipmentOrders;
}
