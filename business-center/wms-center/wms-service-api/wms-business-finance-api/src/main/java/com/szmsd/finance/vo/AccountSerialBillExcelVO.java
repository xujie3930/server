package com.szmsd.finance.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.szmsd.common.plugin.annotation.AutoFieldI18n;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class AccountSerialBillExcelVO implements Serializable {

    @ApiModelProperty(value = "流水号")
    @Excel(name = "流水号",width = 20)
    private String serialNumber;

    @ApiModelProperty(value = "单号")
    @Excel(name = "单号",width = 28)
    private String no;

    @ApiModelProperty(value = "跟踪号")
    @Excel(name = "跟踪号",width = 20)
    private String trackingNo;

    @ApiModelProperty(value = "refNo")
    @Excel(name = "refNo",width = 20)
    private String refNo;

    @ApiModelProperty(value = "客户编码")
    @Excel(name = "客户号",width = 15)
    private String cusCode;

    @AutoFieldI18n
    @ApiModelProperty(value = "币种名")
    @Excel(name = "币种",width = 10)
    private String currencyName;

    @AutoFieldI18n
    @ApiModelProperty(value = "实重")
    @Excel(name = "实重",width = 10)
    private Double weight;

    @AutoFieldI18n
    @ApiModelProperty(value = "计费重")
    @Excel(name = "计费重",width = 10)
    private BigDecimal calcWeight;

    @AutoFieldI18n
    @ApiModelProperty(value = "规格")
    @Excel(name = "规格",width = 20)
    private String specifications;

    @ApiModelProperty(value = "发生额")
    @Excel(name = "发生额",width = 10)
    private BigDecimal amount;

    @AutoFieldI18n
    @ApiModelProperty(value = "业务类别")
    @Excel(name = "业务类别",width = 15)
    private String businessCategory;

    @AutoFieldI18n
    @ApiModelProperty(value = "仓库代码")
    @Excel(name = "仓库代码",width = 15)
    private String warehouseCode;

    @AutoFieldI18n
    @ApiModelProperty(value = "仓库名称")
    @Excel(name = "仓库名称",width = 15)
    private String warehouseName;

    @AutoFieldI18n
    @ApiModelProperty(value = "产品代码")
    @Excel(name = "产品代码",width = 25)
    private String shipmentRule;

    @AutoFieldI18n
    @ApiModelProperty(value = "产品名称")
    @Excel(name = "产品名称",width = 30)
    private String shipmentRuleName;

    @AutoFieldI18n
    @ApiModelProperty(value = "费用类别")
    @Excel(name = "费用类别",width = 15)
    private String chargeCategory;

    @AutoFieldI18n
    @ApiModelProperty(value = "费用类型")
    @Excel(name = "费用类型",width = 15)
    private String chargeType;

    @AutoFieldI18n
    @ApiModelProperty(value = "附注")
    @Excel(name = "附注",width = 40)
    private String fremark;

    @AutoFieldI18n
    @ApiModelProperty(value = "备注")
    @Excel(name = "备注",width = 40)
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "下单时间")
    @Excel(name = "下单时间",format = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8",width = 25)
    private Date orderTime;

    @ApiModelProperty(value = "结算时间")
    @Excel(name = "结算时间",format = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8",width = 25)
    private Date paymentTime;

    @AutoFieldI18n
    @ApiModelProperty(value = "性质")
    @Excel(name = "性质",width = 15)
    private String nature;

    @ApiModelProperty(value = "平台标记单号")
    @Excel(name = "平台标记单号",width = 25)
    private String amazonLogisticsRouteId;

    @ApiModelProperty(value = "国家代码")
    @Excel(name = "国家代码",width = 20)
    private String countryCode;

    @ApiModelProperty(value = "国家名称")
    @Excel(name = "国家名称",width = 20)
    private String country;

    @ApiModelProperty(value = "附注")
    @Excel(name = "附注",width = 40)
    private String note;
}
