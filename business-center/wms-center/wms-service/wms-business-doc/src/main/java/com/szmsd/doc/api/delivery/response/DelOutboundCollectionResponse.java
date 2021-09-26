package com.szmsd.doc.api.delivery.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author zhangyuyuan
 * @date 2021-08-03 9:46
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "DelOutboundCollectionResponse", description = "DelOutboundCollectionResponse对象")
public class DelOutboundCollectionResponse extends DelOutboundResponse {

    @ApiModelProperty(value = "索引", dataType = "Integer")
    private Integer index;
}
