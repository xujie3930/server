package com.szmsd.http.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-12 19:38
 */
@Data
@ApiModel(value = "CancelShipmentOrderBatchResultDto", description = "CancelShipmentOrderBatchResultDto对象")
public class CancelShipmentOrderBatchResultDto implements Serializable {

    @ApiModelProperty(value = "请求唯一标识，可以使用GUID")
    private String referenceNumber;

    @ApiModelProperty(value = "取消订单结果")
    private List<CancelShipmentOrderResultDto> cancelOrders;
}
