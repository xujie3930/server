package com.szmsd.delivery.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-03-05 14:21
 */
@Data
@ApiModel(value = "DelOutboundAgainTrackingNoDto", description = "DelOutboundAgainTrackingNoDto对象")
public class DelOutboundAgainTrackingNoDto implements Serializable {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "出库单号")
    private String orderNo;

    @NotBlank(message = "发货规则不能为空")
    @ApiModelProperty(value = "发货规则（也就是物流承运商，必须填写指定值，例如Fedex, USPS等，相同代表一起交货。）")
    private String shipmentRule;

    @NotNull(message = "地址信息不能为空")
    @ApiModelProperty(value = "地址信息")
    private DelOutboundAddressDto address;

}
