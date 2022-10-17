package com.szmsd.finance.vo.helibao;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class PayCallback implements Serializable {

    @ApiModelProperty(value = "商户编号")
    private String merchantNo;

    @ApiModelProperty(value = "订单编号")
    private String orderNo;

    @ApiModelProperty(value = "产品编号")
    private String productCode;

    @ApiModelProperty(value = "流水号")
    private String serialNumber;

    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @ApiModelProperty(value = "支付金额")
    private BigDecimal orderAmount;

    @ApiModelProperty(value = "订单状态")
    private String orderStatus;

    @ApiModelProperty(value = "订单创建时间")
    private Date createDate;

    @ApiModelProperty(value = "完成时间")
    private Date finishDate;

    @ApiModelProperty(value = "渠道流水号")
    private String consumeOrderId;

    @ApiModelProperty(value = "渠道标识")
    private String chanlType;

    @ApiModelProperty(value = "绑卡ID")
    private String bindId;

    @ApiModelProperty(value = "备注")
    private String remark;
}
