package com.szmsd.doc.api.delivery.response;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zhangyuyuan
 * @date 2021-08-03 9:46
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "DelOutboundShipmentResponse", description = "DelOutboundShipmentResponse对象")
public class DelOutboundShipmentResponse extends DelOutboundResponse {

}
