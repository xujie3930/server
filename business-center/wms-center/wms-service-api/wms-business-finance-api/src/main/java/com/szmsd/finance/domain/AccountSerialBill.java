package com.szmsd.finance.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "流水账单表", description = "流水账单表")
@TableName("fss_account_serial_bill")
public class AccountSerialBill extends FssBaseEntity {

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "单号")
    @Excel(name = "单号")
    private String no;

    @ApiModelProperty(value = "跟踪号")
    @Excel(name = "跟踪号")
    private String trackingNo;

    @ApiModelProperty(value = "refNo")
    @Excel(name = "refNo")
    @TableField(exist = false)
    private String refNo;

    @ApiModelProperty(value = "物流服务名称")
    @Excel(name = "物流服务名称")
    @TableField(exist = false)
    private String shipmentService;

    @ApiModelProperty(value = "客户编码")
    @Excel(name = "客户号")
    private String cusCode;

    @ApiModelProperty(value = "客户名称")
    private String cusName;
    @ApiModelProperty(value = "币种id")
    private String currencyCode;
    @AutoFieldI18n
    @ApiModelProperty(value = "币种名")
    @Excel(name = "币种")
    private String currencyName;

    @AutoFieldI18n
    @ApiModelProperty(value = "实重")
    @Excel(name = "实重")
    @TableField(exist = false)
    private BigDecimal weight;

    @AutoFieldI18n
    @ApiModelProperty(value = "计费重")
    @Excel(name = "计费重")
    @TableField(exist = false)
    private BigDecimal calcWeight;

    @AutoFieldI18n
    @ApiModelProperty(value = "规格")
    @Excel(name = "规格")
    @TableField(exist = false)
    private String specifications;


    @ApiModelProperty(value = "发生额")
    @Excel(name = "发生额")
    private BigDecimal amount;
    //    @FieldJsonI18n(type = RedisLanguageTable.BAS_WAREHOUSE)
    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;
    @AutoFieldI18n
    @ApiModelProperty(value = "仓库名称")
    @Excel(name = "仓库")
    private String warehouseName;
    @AutoFieldI18n
//    @AutoFieldValue(supports = BasSubCommonPlugin.SUPPORTS, code = "096", cp = BasSubValueCommonParameter.class)
    @ApiModelProperty(value = "交易类型")
    private String payMethod;
    @AutoFieldI18n
    @ApiModelProperty(value = "业务类别（性质）")
    @Excel(name = "性质")
    private String businessCategory;
    @ApiModelProperty(value = "产品代码")
    @Excel(name = "产品代码")
    private String productCode;
    @AutoFieldI18n
    @ApiModelProperty(value = "产品类别")
    @Excel(name = "产品类别")
    private String productCategory;
    @AutoFieldI18n
    @ApiModelProperty(value = "费用类别")
    @Excel(name = "费用类别")
    private String chargeCategory;
    @AutoFieldI18n
    @ApiModelProperty(value = "费用类型")
    @Excel(name = "费用类型")
    private String chargeType;

    @AutoFieldI18n
    @ApiModelProperty(value = "备注")
    @Excel(name = "备注")
    private String remark;



    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "下单时间")
    @Excel(name = "下单时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date orderTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "结算时间")
    @Excel(name = "结算时间", dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date paymentTime;

}
