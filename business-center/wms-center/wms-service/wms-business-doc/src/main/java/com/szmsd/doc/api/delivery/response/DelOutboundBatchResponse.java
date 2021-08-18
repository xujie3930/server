package com.szmsd.doc.api.delivery.response;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "DelOutboundBatchResponse", description = "DelOutboundBatchResponse对象")
public class DelOutboundBatchResponse extends DelOutboundResponse {

}
