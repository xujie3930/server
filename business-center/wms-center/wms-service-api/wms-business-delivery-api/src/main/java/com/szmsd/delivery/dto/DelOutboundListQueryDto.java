package com.szmsd.delivery.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-03-05 14:21
 */
@Data
@ApiModel(value = "DelOutboundListQueryDto", description = "DelOutboundListQueryDto对象")
public class DelOutboundListQueryDto implements Serializable {

    @ApiModelProperty(value = "出库单号")
    private String orderNo;

    @ApiModelProperty(value = "采购单号")
    private String purchaseNo;

    @ApiModelProperty(value = "挂号")
    private String trackingNo;

    @ApiModelProperty(value = "发货规则（也就是物流承运商，必须填写指定值，例如Fedex, USPS等，相同代表一起交货。）")
    private String shipmentRule;

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "订单状态")
    private String state;

    @ApiModelProperty(value = "客户代码")
    private String customCode;

    @ApiModelProperty(value = "出库订单类型")
    private String orderType;

    @ApiModelProperty(value = "创建时间")
    private String[] createTimes;

    @ApiModelProperty(value = "修改时间")
    private String updateTime;
}
