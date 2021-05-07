package com.szmsd.finance.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "DelOutboundChargeQueryDto", description = "DelOutboundChargeQueryDto对象")
public class QueryChargeDto implements Serializable {

    @ApiModelProperty(value = "当前起始页索引")
    private int pageNum;

    @ApiModelProperty(value = "每页显示记录数")
    private int pageSize;

    @ApiModelProperty(value = "查询费用类型 0：出库费 1：操作费")
    private int queryType;

    @ApiModelProperty(value = "客户编码")
    private String customCode;

    @ApiModelProperty(value = "支付类型")
    private String payMethod;

    @ApiModelProperty(value = "处理号/挂号/出库单号")
    private String no;

    @ApiModelProperty(value = "物流服务（也就是物流承运商，必须填写指定值，例如Fedex, USPS等，相同代表一起交货。）")
    private String shipmentRule;

    @ApiModelProperty(value = "下单时间开始")
    private String orderTimeStart;

    @ApiModelProperty(value = "下单时间结束")
    private String orderTimeEnd;

}
