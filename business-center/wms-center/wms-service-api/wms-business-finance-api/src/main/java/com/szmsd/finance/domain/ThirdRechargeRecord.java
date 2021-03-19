package com.szmsd.finance.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author liulei
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "第三方充值记录", description = "第三方充值记录")
@TableName("fss_third_recharge_record")
public class ThirdRechargeRecord extends FssBaseEntity {
    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "客户id")
    private Long cusId;

    @ApiModelProperty(value = "客户编码")
    private String cusCode;

    @ApiModelProperty(value = "客户名称")
    private String cusName;

    @ApiModelProperty(value = "充值编号")
    @TableField(value = "recharge_no")
    private String rechargeNo;

    @ApiModelProperty(value = "充值金额")
    @TableField(value = "recharge_amount")
    private BigDecimal rechargeAmount;

    @ApiModelProperty(value = "充值币种")
    @TableField(value = "recharge_currency")
    private String rechargeCurrency;

    @ApiModelProperty(value = "手续费")
    @TableField(value = "transaction_amount")
    private BigDecimal transactionAmount;

    @ApiModelProperty(value = "手续费币种")
    @TableField(value = "transaction_currency")
    private String transactionCurrency;

    @ApiModelProperty(value = "实际到账金额")
    @TableField(value = "actual_amount")
    private BigDecimal actualAmount;

    @ApiModelProperty(value = "实际到账金额币种")
    @TableField(value = "actual_currency")
    private String actualCurrency;

    @ApiModelProperty(value = "充值状态")
    @TableField(value = "recharge_status")
    private String rechargeStatus;

    @ApiModelProperty(value = "请求参数唯一标识/流水号")
    private String serialNo;

    @ApiModelProperty(value = "错误编码")
    private String errorCode;

    @ApiModelProperty(value = "错误信息")
    private String errorMessage;

}
