package com.szmsd.finance.dto;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.finance.enums.CreditConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * @ClassName: CreditInfoBO
 * @Description: 授信额度信息
 * @Author: 11
 * @Date: 2021-09-07 14:56
 */
@Setter
@Getter
@ApiModel(description = "授信额度信息")
public class CreditInfoUseBO {

    @ApiModelProperty(value = "订单号")
    @Excel(name = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "交易类型")
    @Excel(name = "交易类型")
    private String payMethod;

    @ApiModelProperty(value = "产品类别")
    @Excel(name = "产品类别")
    private String productCategory;

    @ApiModelProperty(value = "费用类别")
    @Excel(name = "费用类别")
    private String chargeCategory;

    @ApiModelProperty(value = "费用类型")
    @Excel(name = "费用类型")
    private String chargeType;

    @ApiModelProperty(value = "订单金额")
    @Excel(name = "订单金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "订单币别")
    @Excel(name = "订单币别")
    private String currencyCode;

    @ApiModelProperty(value = "实际扣费金额(余额扣费)")
    @Excel(name = "实际扣费金额(余额扣费)")
    private BigDecimal actualDeduction;

    @ApiModelProperty(value = "使用授信金额")
    @Excel(name = "使用授信金额")
    private BigDecimal creditUseAmount;

    @ApiModelProperty(value = "使用授信类型")
    @Excel(name = "使用授信类型")
    private Integer creditType;

    @ApiModelProperty(value = "授信开始时间")
    @Excel(name = "授信开始时间")
    private Date creditBeginTime;

    @ApiModelProperty(value = "授信结束时间")
    @Excel(name = "授信结束时间")
    private Date creditEndTime;

    @ApiModelProperty(value = "还款金额")
    @Excel(name = "还款金额")
    private BigDecimal repaymentAmount;

    @ApiModelProperty(value = "剩余还款金额")
    @Excel(name = "剩余还款金额")
    private BigDecimal remainingRepaymentAmount;

    @ApiModelProperty(value = "扣费类型（0=扣款,1=还款）")
    @Excel(name = "扣费类型（0=扣款,1=还款）")
    private Integer type;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
