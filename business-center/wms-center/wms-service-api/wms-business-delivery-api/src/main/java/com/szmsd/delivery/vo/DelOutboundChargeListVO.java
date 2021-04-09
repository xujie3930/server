package com.szmsd.delivery.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(value = "DelOutboundChargeListVO", description = "DelOutboundChargeListVO对象")
public class DelOutboundChargeListVO implements Serializable {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "时间")
    private String createTime;

    @ApiModelProperty(value = "发货规则（也就是物流承运商，必须填写指定值，例如Fedex, USPS等，相同代表一起交货。）")
    private String shipmentRule;

    @ApiModelProperty(value = "挂号")
    private String trackingNo;

    @ApiModelProperty(value = "出库号")
    private String orderNo;

    @ApiModelProperty(value = "国家名称")
    private String country;

    @ApiModelProperty(value = "件数")
    private Long qty;

    @ApiModelProperty(value = "实际重量 g")
    private Double weight;

    @ApiModelProperty(value = "费用")
    private BigDecimal amount;

    @ApiModelProperty(value = "基础运费")
    private BigDecimal baseShippingFee = BigDecimal.ZERO;

    @ApiModelProperty(value = "偏远地区费")
    private BigDecimal remoteAreaSurcharge = BigDecimal.ZERO;

    @ApiModelProperty(value = "超大附加费")
    private BigDecimal overSizeSurcharge = BigDecimal.ZERO;

    @ApiModelProperty(value = "燃油费")
    private BigDecimal fuelCharge = BigDecimal.ZERO;

    @ApiModelProperty(value = "单据完成处理状态")
    private String completedState;

    @ApiModelProperty(value = "备注")
    private String remark;

}
