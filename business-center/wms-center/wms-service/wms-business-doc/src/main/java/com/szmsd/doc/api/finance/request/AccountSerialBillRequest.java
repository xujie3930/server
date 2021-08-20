package com.szmsd.doc.api.finance.request;

import com.szmsd.common.core.web.controller.QueryDto;
import com.szmsd.finance.enums.BillEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountSerialBillRequest extends QueryDto {

    @Size(max = 30)
    @ApiModelProperty(value = "单号 [30]")
    private String no;

    @Size(max = 30)
    @ApiModelProperty(value = "跟踪号 [30]")
    private String trackingNo;

    @NotBlank
    @Size(max = 30)
    @ApiModelProperty(value = "客户编码 [30]",required = true)
    private String cusCode;
    @Size(max = 200)
    @ApiModelProperty(value = "客户名称 [200]")
    private String cusName;
    @Size(max = 30)
    @ApiModelProperty(value = "币种编号 [30]")
    private String currencyCode;
    @Size(max = 200)
    @ApiModelProperty(value = "币种名 [200]")
    private String currencyName;

    @ApiModelProperty(value = "发生额")
    private BigDecimal amount;
    @Size(max = 30)
    @ApiModelProperty(value = "仓库代码 [30]")
    private String warehouseCode;
    @Size(max = 200)
    @ApiModelProperty(value = "仓库名称 [200]")
    private String warehouseName;

    @ApiModelProperty(value = "交易类型 ONLINE_INCOME(在线充值),OFFLINE_INCOME(线下充值)" +
            ",EXCHANGE_INCOME(汇率转换充值),EXCHANGE_PAYMENT(汇率转换扣款)" +
            ",WITHDRAW_PAYMENT(提现),SPECIAL_OPERATE(特殊操作)" +
            ",BUSINESS_OPERATE(业务操作),WAREHOUSE_RENT(仓租)" +
            ",BALANCE_FREEZE(余额冻结)" +
            ",BALANCE_THAW(余额解冻)" +
            ",BALANCE_DEDUCTIONS(费用扣除)" +
            ",BALANCE_EXCHANGE(余额转换)")
    private BillEnum.PayMethod payMethod = BillEnum.PayMethod.ONLINE_INCOME;
    @Size(max = 200)
    @ApiModelProperty(value = "业务类别（性质）[200]")
    private String businessCategory;
    @Size(max = 30)
    @ApiModelProperty(value = "产品代码 [30]")
    private String productCode;
    @Size(max = 200)
    @ApiModelProperty(value = "产品类别 [200]")
    private String productCategory;
    @Size(max = 200)
    @ApiModelProperty(value = "费用类别 [200]")
    private String chargeCategory;
    @Size(max = 200)
    @ApiModelProperty(value = "费用类型 [200]")
    private String chargeType;

    @ApiModelProperty(value = "下单时间")
    private Date orderTime;

    @ApiModelProperty(value = "结算时间")
    private Date paymentTime;

    @ApiModelProperty(value = "计算时间开始",example = "yyyy-MM-dd HH:mm:ss")
    private String createTimeStart;

    @ApiModelProperty(value = "计算时间结束",example = "yyyy-MM-dd HH:mm:ss")
    private String createTimeEnd;
    @Size(max = 500)
    @ApiModelProperty(value = "备注 [500]")
    private String remark;

}
