package com.szmsd.doc.api.delivery.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-07-28 16:07
 */
@Data
@ApiModel(value = "DelOutboundLabelRequest", description = "DelOutboundLabelRequest对象")
public class DelOutboundLabelRequest implements Serializable {

    @NotNull(message = "订单号不能为空")
    @ApiModelProperty(value = "订单号", required = true, dataType = "String", position = 1, example = "[\"XF0001\"]")
    private List<String> orderNos;

    @ApiModelProperty(value = "标签类型 0.原 1.供应商尾程标签",dataType = "String", position = 1, example = "0")
    private String type;


}
