package com.szmsd.doc.api.delivery.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(value = "DelOutboundBatchRequest", description = "DelOutboundBatchRequest对象")
public class DelOutboundBatchRequest implements Serializable {

    @ApiModelProperty(value = "出货渠道", dataType = "String", position = 1, example = "")
    private String shipmentChannel;

    @ApiModelProperty(value = "是否优先发货", dataType = "Boolean", position = 2, example = "false")
    private Boolean isFirst;

    @ApiModelProperty(value = "是否必须按要求装箱", dataType = "Boolean", position = 3, example = "false")
    private Boolean isPackingByRequired;

    @ApiModelProperty(value = "是否默认仓库装箱数据", dataType = "Boolean", position = 4, example = "false")
    private Boolean isDefaultWarehouse;

    @Max(value = Integer.MAX_VALUE, message = "装箱数量不能大于2147483647")
    @Min(value = 1, message = "装箱数量不能小于1")
    @ApiModelProperty(value = "装箱数量", dataType = "Long", position = 5, example = "0")
    private Long boxNumber;

    @ApiModelProperty(value = "是否贴箱标", dataType = "Boolean", position = 6, example = "false")
    private Boolean isLabelBox;

    @Size(max = 50, message = "增值税号不能超过50个字符")
    @ApiModelProperty(value = "增值税号", dataType = "String", position = 7, example = "F00X")
    private String ioss;

    @ApiModelProperty(value = "提货方式", dataType = "String", position = 8, example = "")
    private String deliveryMethod;

    @ApiModelProperty(value = "提货时间", dataType = "Date", position = 9, example = "")
    private Date deliveryTime;

    @Size(max = 200, message = "提货商/快递商不能超过200个字符")
    @ApiModelProperty(value = "提货商/快递商", dataType = "String", position = 10, example = "")
    private String deliveryAgent;

    @Size(max = 200, message = "提货/快递信息不能超过200个字符")
    @ApiModelProperty(value = "提货/快递信息", dataType = "String", position = 11, example = "")
    private String deliveryInfo;

    @NotBlank(message = "物流服务不能为空")
    @Size(max = 50, message = "物流服务不能超过50个字符")
    @ApiModelProperty(value = "物流服务", dataType = "String", position = 12, example = "")
    private String shipmentRule;

    @Size(max = 50, message = "参考号不能超过50个字符")
    @ApiModelProperty(value = "参考号", dataType = "String", position = 13, example = "")
    private String refNo;

    @Size(max = 500, message = "备注不能超过500个字符")
    @ApiModelProperty(value = "备注", dataType = "String", position = 14, example = "")
    private String remark;

    @Valid
    @ApiModelProperty(value = "地址信息", dataType = "DelOutboundAddressRequest", position = 15)
    private DelOutboundAddressRequest address;

    @Valid
    @ApiModelProperty(value = "明细信息", dataType = "DelOutboundDetailRequest", position = 16)
    private List<DelOutboundDetailRequest> details;

}