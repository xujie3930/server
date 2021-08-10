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
@ApiModel(value = "DelOutboundDestroyListRequest", description = "DelOutboundDestroyListRequest对象")
public class DelOutboundDestroyListRequest implements Serializable {

    @Valid
    @ApiModelProperty(value = "销毁单据集合", required = true, dataType = "DelOutboundDestroyRequest", position = 1)
    private List<DelOutboundDestroyRequest> requestList;
}
