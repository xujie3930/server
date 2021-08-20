package com.szmsd.doc.api.delivery.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "DelOutboundSelfPickListRequest", description = "DelOutboundSelfPickListRequest对象")
public class DelOutboundSelfPickListRequest implements Serializable {

    @Valid
    @ApiModelProperty(value = "单据集合", required = true, dataType = "DelOutboundSelfPickRequest", position = 1)
    private List<DelOutboundSelfPickRequest> requestList;

}
