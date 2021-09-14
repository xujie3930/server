package com.szmsd.doc.api.finance.request;

import com.szmsd.common.core.web.controller.QueryDto;
import com.szmsd.doc.api.SwaggerDictionary;
import com.szmsd.finance.enums.BillEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountSerialBillRequest extends QueryDto {

    @Size(max = 30, message = "订单号最大支持30字符")
    @ApiModelProperty(value = "订单号 [30]", example = "975374118912")
    private String no;

//    @Size(max = 30, message = "跟踪号最大支持30字符")
//    @ApiModelProperty(value = "跟踪号 [30]")
//    private String trackingNo;

    @NotBlank(message = "客户编码不能为空")
    @Size(max = 30, message = "客户编码最大支持30字符")
    @ApiModelProperty(value = "客户编码 [30]", example = "CNYWO7", required = true)
    private String cusCode;
    //    @Size(max = 200, message = "客户名称最大支持200字符")
//    @ApiModelProperty(value = "客户名称 [200]")
//    private String cusName;
    @SwaggerDictionary(dicCode = "008")
    @Size(max = 30, message = "币种编号最大支持30字符")
    @ApiModelProperty(value = "币种编号 主子类别 008 [30]", example = "CNY")
    private String currencyCode;
    //    @Size(max = 200, message = "币种名最大支持200字符")
//    @ApiModelProperty(value = "币种名 [200]")
//    private String currencyName;
//    @DecimalMin(value = "0", message = "发生额不能小于0")
//    @ApiModelProperty(value = "发生额")
//    private BigDecimal amount;
    @Size(max = 30, message = "仓库代码最大支持30字符")
    @ApiModelProperty(value = "仓库代码 /warehouse/page [30]", example = "NJ")
    private String warehouseCode;
//    @Size(max = 200, message = "仓库名称最大支持200字符")
//    @ApiModelProperty(value = "仓库名称 [200]")
//    private String warehouseName;

    //    @ApiModelProperty(value = "交易类型 ONLINE_INCOME(在线充值),OFFLINE_INCOME(线下充值)" +
//            ",EXCHANGE_INCOME(汇率转换充值),EXCHANGE_PAYMENT(汇率转换扣款)" +
//            ",WITHDRAW_PAYMENT(提现),SPECIAL_OPERATE(特殊操作)" +
//            ",BUSINESS_OPERATE(业务操作),WAREHOUSE_RENT(仓租)" +
//            ",BALANCE_FREEZE(余额冻结)" +
//            ",BALANCE_THAW(余额解冻)" +
//            ",BALANCE_DEDUCTIONS(费用扣除)" +
//            ",BALANCE_EXCHANGE(余额转换)", example = "ONLINE_INCOME")
//    private BillEnum.PayMethod payMethod = BillEnum.PayMethod.ONLINE_INCOME;
    @Size(max = 200, message = "性质 最大支持200字符")
    @ApiModelProperty(value = "性质 (文本输入) [200]", example = "物流基础费")
    private String businessCategory;
    @Size(max = 30, message = "产品代码最大支持30字符")
    @ApiModelProperty(value = "产品代码 (文本输入)[30]", example = "DSLOCO2")
    private String productCode;
    @Size(max = 200, message = "产品类别最大支持200字符")
    @ApiModelProperty(value = "产品类别 (文本输入)[200]")
    private String productCategory;
    @Size(max = 200, message = "费用类别最大支持200字符")
    @ApiModelProperty(value = "费用类别 (文本输入) [200]", example = "物流基础费")
    private String chargeCategory;
    @Size(max = 200, message = "费用类型最大支持200字符")
    @ApiModelProperty(value = "费用类型 (文本输入) [200]", example = "物流基础费")
    private String chargeType;

//    @ApiModelProperty(value = "下单时间 yyyy-MM-dd HH:mm:ss", example = "yyyy-MM-dd HH:mm:ss")
//    private Date orderTime;
//
//    @ApiModelProperty(value = "结算时间 yyyy-MM-dd HH:mm:ss", example = "yyyy-MM-dd HH:mm:ss")
//    private Date paymentTime;

    @ApiModelProperty(value = "结算时间开始 yyyy-MM-dd HH:mm:ss", example = "yyyy-MM-dd HH:mm:ss")
    private String createTimeStart;

    @ApiModelProperty(value = "结算时间结束 yyyy-MM-dd HH:mm:ss", example = "yyyy-MM-dd HH:mm:ss")
    private String createTimeEnd;
//    @Size(max = 500, message = "备注最大支持500字符")
//    @ApiModelProperty(value = "备注 [500]")
//    private String remark;

}
