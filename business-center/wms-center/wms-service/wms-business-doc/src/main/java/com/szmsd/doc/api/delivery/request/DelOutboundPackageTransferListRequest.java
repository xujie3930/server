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
@ApiModel(value = "DelOutboundPackageTransferListRequest", description = "DelOutboundPackageTransferListRequest对象")
public class DelOutboundPackageTransferListRequest implements Serializable {

    @Valid
    @ApiModelProperty(value = "转运单集合", required = true, dataType = "DelOutboundPackageTransferRequest", position = 1)
    private List<DelOutboundPackageTransferRequest> requestList;
}
