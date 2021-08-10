package com.szmsd.doc.api.delivery.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-08-03 15:29
 */
@Data
@ApiModel(value = "DelOutboundShipmentListRequest", description = "DelOutboundShipmentListRequest对象")
public class DelOutboundShipmentListRequest implements Serializable {

    @Valid
    @ApiModelProperty(value = "一件代发单据集合", required = true, dataType = "DelOutboundShipmentRequest", position = 1)
    private List<DelOutboundShipmentRequest> requestList;
}
