package com.szmsd.finance.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.plugin.annotation.AutoFieldI18n;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
@ApiModel(value = "流水账单表EN", description = "流水账单表EN")
public class AccountSerialBillEn{

    @ApiModelProperty(value = "单号")
    @Excel(name = "Order Number")
    private String no;

    @ApiModelProperty(value = "跟踪号")
    @Excel(name = "Tracking Number")
    private String trackingNo;

    @ApiModelProperty(value = "客户编码")
    @Excel(name = "Customer Code")
    private String cusCode;

    @AutoFieldI18n
    @ApiModelProperty(value = "币种名")
    @Excel(name = "Currency")
    private String currencyCode;

    @ApiModelProperty(value = "发生额")
    @Excel(name = "Cost")
    private BigDecimal amount;


    //    @FieldJsonI18n(type = RedisLanguageTable.BAS_WAREHOUSE)
    @Excel(name = "Warehouse")
    private String warehouseCode;


    @AutoFieldI18n
    @ApiModelProperty(value = "业务类别（性质）")
    @Excel(name = "Nature(Property)")
    private String businessCategory;

    @Excel(name = "Logistics Code")
    private String logisticsCode;



    @ApiModelProperty(value = "产品代码")
    @Excel(name = "Product Code")
    private String productCode;
    @AutoFieldI18n
    @ApiModelProperty(value = "费用类别")
    @Excel(name = "Cost Code")
    private String chargeCategory;
    @AutoFieldI18n
    @ApiModelProperty(value = "费用类型")
    @Excel(name = "Type Of Cost")
    private String chargeType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "下单时间")
    @Excel(name = "Order Time", dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date orderTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "结算时间")
    @Excel(name = "Settlement Time", dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date paymentTime;

}
