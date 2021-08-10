package com.szmsd.doc.api.finance.response;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.finance.domain.FssBaseEntity;
import com.szmsd.finance.enums.BillEnum;
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
@ApiModel(value = "流水账单相应数据", description = "流水账单相应数据")
public class AccountSerialBillResponse extends FssBaseEntity {

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "单号")
    @Excel(name = "单号")
    private String no;

    @ApiModelProperty(value = "跟踪号")
    @Excel(name = "跟踪号")
    private String trackingNo;

    @ApiModelProperty(value = "客户编码")
    @Excel(name = "客户号")
    private String cusCode;

    @ApiModelProperty(value = "客户名称")
    private String cusName;

    @ApiModelProperty(value = "币种id")
    private String currencyCode;

    @ApiModelProperty(value = "币种名")
    @Excel(name = "币种")
    private String currencyName;

    @ApiModelProperty(value = "发生额")
    @Excel(name = "发生额")
    private BigDecimal amount;

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "仓库名称")
    @Excel(name = "仓库")
    private String warehouseName;

    @ApiModelProperty(value = "交易类型")
    private BillEnum.PayMethod payMethod;

    @ApiModelProperty(value = "业务类别（性质）")
    @Excel(name = "性质")
    private String businessCategory;

    @ApiModelProperty(value = "产品代码")
    @Excel(name = "产品代码")
    private String productCode;

    @ApiModelProperty(value = "产品类别")
    @Excel(name = "产品类别")
    private String productCategory;

    @ApiModelProperty(value = "费用类别")
    @Excel(name = "费用类别")
    private String chargeCategory;

    @ApiModelProperty(value = "费用类型")
    @Excel(name = "费用类型")
    private String chargeType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "下单时间")
    @Excel(name = "下单时间")
    private Date orderTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "结算时间")
    @Excel(name = "结算时间")
    private Date paymentTime;

}
