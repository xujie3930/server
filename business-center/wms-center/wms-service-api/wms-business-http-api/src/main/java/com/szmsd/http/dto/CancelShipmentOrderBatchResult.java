package com.szmsd.http.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-04-07 11:26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelShipmentOrderBatchResult implements Serializable {

    @ApiModelProperty(value = "请求唯一标识，可以使用GUID")
    private String referenceNumber;

    @ApiModelProperty(value = "取消订单结果")
    private List<CancelShipmentOrderResult> cancelOrders;
}
