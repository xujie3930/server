package com.szmsd.doc.api.delivery.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(value = "DelOutboundSelfPickRequest", description = "DelOutboundSelfPickRequest对象")
public class DelOutboundSelfPickRequest implements Serializable {

    @ApiModelProperty(value = "是否优先发货", dataType = "Boolean", position = 1, example = "false")
    private Boolean isFirst;

    @ApiModelProperty(value = "提货方式", dataType = "String", position = 2, example = "")
    private String deliveryMethod;

    @ApiModelProperty(value = "提货时间", dataType = "Date", position = 3, example = "")
    private Date deliveryTime;

    @Size(max = 200, message = "提货商/快递商不能超过200个字符")
    @ApiModelProperty(value = "提货商/快递商", dataType = "String", position = 4, example = "")
    private String deliveryAgent;

    @Size(max = 200, message = "提货/快递信息不能超过200个字符")
    @ApiModelProperty(value = "提货/快递信息", dataType = "String", position = 5, example = "")
    private String deliveryInfo;

    @Size(max = 50, message = "参考号不能超过50个字符")
    @ApiModelProperty(value = "参考号", dataType = "String", position = 6, example = "")
    private String refNo;

    @Size(max = 50, message = "增值税号不能超过50个字符")
    @ApiModelProperty(value = "增值税号", dataType = "String", position = 7, example = "F00X")
    private String ioss;

    @Size(max = 500, message = "备注不能超过500个字符")
    @ApiModelProperty(value = "备注", dataType = "String", position = 8, example = "")
    private String remark;

    @Valid
    @ApiModelProperty(value = "明细信息", dataType = "DelOutboundDetailRequest", position = 9)
    private List<DelOutboundDetailRequest> details;

}
