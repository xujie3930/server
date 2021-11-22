package com.szmsd.finance.dto;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.finance.enums.BillEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 支付实体
 * @author liulei
 */
@Data
public class CustPayDTO {
    @ApiModelProperty(value = "客户id")
    private Long cusId;

    @ApiModelProperty(value = "客户编码")
    private String cusCode;

    @ApiModelProperty(value = "客户名称")
    private String cusName;

    @ApiModelProperty(value = "支付类型")
    private BillEnum.PayType payType;

    @ApiModelProperty(value = "支付方式")
    private BillEnum.PayMethod payMethod;

    @ApiModelProperty(value = "订单类型")
    private String orderType;

    @ApiModelProperty(value = "币种编码")
    private String currencyCode;

    @ApiModelProperty(value = "币种名")
    private String currencyName;

    @ApiModelProperty(value = "金额")
    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal amount;

    @ApiModelProperty(value = "比率")
    private BigDecimal rate;

    @ApiModelProperty(value = "币种code2")
    private String currencyCode2;

    @ApiModelProperty(value = "币种名2")
    private String currencyName2;

    @ApiModelProperty(value = "充值渠道")
    private String method;

    @ApiModelProperty(value = "银联银行代码")
    private String bankCode;

    @ApiModelProperty(value = "单号")
    private String no;

    @ApiModelProperty(value = "下单时间")
    private Date orderTime;

    @ApiModelProperty(value = "充值说明")
    private String remark;

    @ApiModelProperty(value = "仓库编号")
    private String warehouseCode;
    @ApiModelProperty(value = "仓库名称")
    private String warehouseName;

    @ApiModelProperty(value = "详情")
    private List<AccountSerialBillDTO> serialBillInfoList;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
