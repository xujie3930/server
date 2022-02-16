package com.szmsd.finance.dto;

import com.szmsd.common.core.utils.StringToolkit;
import com.szmsd.common.core.utils.StringUtils;
import com.szmsd.finance.enums.BillEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountSerialBillDTO {

    @ApiModelProperty(value = "当前起始页索引")
    private int pageNum;

    @ApiModelProperty(value = "每页显示记录数")
    private int pageSize;

    @ApiModelProperty(value = "单号")
    private String no;

    public void setNo(String no) {
        this.no = no;
        Optional.ofNullable(no).filter(StringUtils::isNotBlank).ifPresent(x -> this.noList = StringToolkit.getCodeByArray(x));
    }

    @ApiModelProperty(value = "单号")
    private List<String> noList;

    @ApiModelProperty(value = "跟踪号")
    private String trackingNo;

    @ApiModelProperty(value = "客户编码")
    private String cusCode;

    public void setCusCode(String cusCode) {
        this.cusCode = cusCode;
        Optional.ofNullable(cusCode).filter(StringUtils::isNotBlank).ifPresent(x -> this.cusCodeList = StringToolkit.getCodeByArray(x));
    }

    @ApiModelProperty(value = "客户编码")
    private List<String> cusCodeList;

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
    /**
     * #{@link BillEnum.NatureEnum}
     */
    @ApiModelProperty(value = "业务类别（性质）")
    private String businessCategory;

    public void setBusinessCategory(String businessCategory) {
        this.businessCategory = businessCategory;
        if (StringUtils.isNotBlank(businessCategory)) {
            // 这些需要转换成 费用扣除
            List<String> category = Arrays.asList("物料费", "操作费", "物流基础费", "处理费", "其他费");
            if (category.contains(businessCategory)) {
                this.businessCategory = BillEnum.NatureEnum.EXPENSE_DEDUCTION.getName();
            }
        }
    }

    @ApiModelProperty(value = "产品代码")
    private String productCode;

    public void setProductCode(String productCode) {
        this.productCode = productCode;
        Optional.ofNullable(productCode).filter(StringUtils::isNotBlank).ifPresent(x -> this.productCodeList = StringToolkit.getCodeByArray(x));
    }

    @ApiModelProperty(value = "产品代码")
    private List<String> productCodeList;
    /**
     * #{@link BillEnum.ProductCategoryEnum}
     */
    @ApiModelProperty(value = "产品类别")
    private String productCategory;

    @ApiModelProperty(value = "费用类别")
    private String chargeCategory;

    @ApiModelProperty(value = "费用类型")
    private String chargeType;

    @ApiModelProperty(value = "下单时间")
    private Date orderTime;

    @ApiModelProperty(value = "结算时间")
    private Date paymentTime;

    @ApiModelProperty(value = "计算时间开始")
    private String createTimeStart;

    @ApiModelProperty(value = "计算时间结束")
    private String createTimeEnd;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "选择导出的id")
    private String ids;

    public AccountSerialBillDTO(CustPayDTO dto, AccountSerialBillDTO details) {
        this.no = dto.getNo();
        this.trackingNo = details.getTrackingNo();
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
        this.orderTime = details.getOrderTime();
        this.paymentTime = details.getPaymentTime();
        this.remark = details.getRemark();
    }
}
