package com.szmsd.finance.dto;

import com.szmsd.finance.enums.BillEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountSerialBillDTO {

    @ApiModelProperty(value = "单号")
    private String no;

    @ApiModelProperty(value = "跟踪号")
    private String trackingNo;

    @ApiModelProperty(value = "客户编码")
    private String cusCode;

    @ApiModelProperty(value = "客户名称")
    private String cusName;

    @ApiModelProperty(value = "币种id")
    private String currencyCode;

    @ApiModelProperty(value = "币种名")
    private String currencyName;

    @ApiModelProperty(value = "发生额")
    private BigDecimal amount;

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "仓库名称")
    private String warehouseName;

    @ApiModelProperty(value = "交易类型")
    private BillEnum.PayMethod payMethod;

    @ApiModelProperty(value = "业务类别（性质）")
    private String businessCategory;

    @ApiModelProperty(value = "产品代码")
    private String productCode;

    @ApiModelProperty(value = "产品类别")
    private String productCategory;

    @ApiModelProperty(value = "费用类别")
    private String chargeCategory;

    @ApiModelProperty(value = "费用类型")
    private String chargeType;

    @ApiModelProperty(value = "计算时间开始")
    private String createTimeStart;

    @ApiModelProperty(value = "计算时间结束")
    private String createTimeEnd;

    @ApiModelProperty(value = "备注")
    private String remark;

    public AccountSerialBillDTO(CustPayDTO dto, AccountSerialBillDTO details) {
        this.no = dto.getNo();
        this.cusCode = dto.getCusCode();
        this.cusName = dto.getCusName();
        this.currencyCode = details.getCurrencyCode();
        this.currencyName = details.getCurrencyName();
        this.amount = details.getAmount();
        this.warehouseCode = details.getWarehouseCode();
        this.warehouseName = details.getWarehouseName();
        this.payMethod = dto.getPayMethod();
        this.businessCategory = details.getBusinessCategory();
        this.productCode = details.getProductCode();
        this.productCategory = details.getProductCategory();
        this.chargeCategory = details.getChargeCategory();
        this.chargeType = details.getChargeType();
        this.remark = details.getRemark();
    }
}
