package com.szmsd.finance.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@ApiModel(value = "账户支付记录表", description = "账户支付记录表")
@TableName("fss_account_pay")
public class AccountPay extends FssBaseEntity {

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "商户编号")
    private String merchantNo;

    @ApiModelProperty(value = "订单编号")
    private String orderNo;

    @ApiModelProperty(value = "产品编号")
    private String productCode;

    @ApiModelProperty(value = "流水号")
    private String serialNumber;

    @ApiModelProperty(value = "二维码url")
    private String qrCode;

    @ApiModelProperty(value = "客户代码")
    private String cusCode;

    @ApiModelProperty(value = "支付金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "手续费")
    private BigDecimal procedureAmount;

    @ApiModelProperty(value = "实际支付金额")
    private BigDecimal acturlAmount;

    @ApiModelProperty(value = "商品名称")
    private String goodsName;

    @ApiModelProperty(value = "二维码生成时间")
    private Date generatorTime;

    @ApiModelProperty(value = "错误编号")
    private String errorCode;

    @ApiModelProperty(value = "错误信息")
    private String errorMessage;

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

    @ApiModelProperty(value = "回调次数")
    private Long callbackNumber;



}
