package com.szmsd.doc.api.delivery.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "DelOutboundBatchListRequest", description = "DelOutboundBatchListRequest对象")
public class DelOutboundBatchListRequest implements Serializable {

    @Valid
    @ApiModelProperty(value = "单据集合", required = true, dataType = "DelOutboundBatchRequest", position = 1)
    private List<DelOutboundBatchRequest> requestList;
}
