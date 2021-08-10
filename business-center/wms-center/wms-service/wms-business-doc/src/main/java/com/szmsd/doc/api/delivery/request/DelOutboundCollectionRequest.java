package com.szmsd.doc.api.delivery.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "DelOutboundCollectionRequest", description = "DelOutboundCollectionRequest对象")
public class DelOutboundCollectionRequest implements Serializable {

    @ApiModelProperty(value = "是否优先发货", dataType = "Boolean", position = 1, example = "false")
    private Boolean isFirst;

    @Size(max = 50, message = "增值税号不能超过50个字符")
    @ApiModelProperty(value = "增值税号", dataType = "String", position = 2, example = "F00X")
    private String ioss;

    @NotBlank(message = "物流服务不能为空")
    @Size(max = 50, message = "物流服务不能超过50个字符")
    @ApiModelProperty(value = "物流服务", dataType = "String", position = 3, example = "FX")
    private String shipmentRule;

    @Size(max = 50, message = "参考号不能超过50个字符")
    @ApiModelProperty(value = "参考号", dataType = "String", position = 4, example = "")
    private String refNo;

    @Size(max = 500, message = "备注不能超过500个字符")
    @ApiModelProperty(value = "备注", dataType = "String", position = 5, example = "")
    private String remark;

    @Valid
    @ApiModelProperty(value = "地址信息", dataType = "DelOutboundAddressRequest", position = 6)
    private DelOutboundAddressRequest address;

    @Valid
    @ApiModelProperty(value = "明细信息", dataType = "DelOutboundDetailRequest", position = 7)
    private List<DelOutboundDetailRequest> details;

}
