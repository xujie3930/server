package com.szmsd.doc.api.delivery.response;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 转运出库
 *
 * @author zhangyuyuan
 * @date 2021-08-03 9:46
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "DelOutboundPackageTransferResponse", description = "DelOutboundPackageTransferResponse对象")
public class DelOutboundPackageTransferResponse extends DelOutboundResponse {

}
