package com.szmsd.finance.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(description = "国内直发excel数据返回")
public class BillDirectDeliveryTotalVO implements Serializable {

    @ApiModelProperty(value = "性质")
    private String businessCategory;

    @ApiModelProperty(value = "单号")
    private String orderNo;

    @ApiModelProperty(value = "跟踪号")
    private String trackingNo;

    @ApiModelProperty(value = "规格")
    private String specifications;

    @ApiModelProperty(value = "币种")
    private String currencyCode;

    @ApiModelProperty(value = "refNo")
    private String refNo;

    @ApiModelProperty(value = "客户代码")
    private String cusCode;

    @ApiModelProperty(value = "业务类型")
    private String orderType;

    @ApiModelProperty(value = "发货仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "目的地")
    private String destinationDelivery;

    @ApiModelProperty(value = "目的国家")
    private String country;

    @ApiModelProperty(value = "出库产品代码")
    private String shipmentRule;

    @ApiModelProperty(value = "出库产品名称")
    private String shipmentService;

    @ApiModelProperty(value = "出库产品名称")
    private String shipmentRuleName;

    @ApiModelProperty(value = "产品名称")
    private String productName;

    @ApiModelProperty(value = "平台标记单号")
    private String amazonLogisticsRouteId;

    @ApiModelProperty(value = "sku")
    private String sku;

    @ApiModelProperty(value = "仓库核重重量")
    private String weight;

    @ApiModelProperty(value = "客户下单重")
    private String forecastWeight;

    @ApiModelProperty(value = "计量重")
    private BigDecimal calcWeight;

    @ApiModelProperty(value = "计量重单位")
    private String calcWeightUnit;

    @ApiModelProperty(value = "计量重+单位")
    private String resultCalcWeight;

    @ApiModelProperty(value = "订单创建时间")
    private String createTime;

    @ApiModelProperty(value = "结算时间")
    private String paymentTime;

    @ApiModelProperty(value = "合计")
    private BigDecimal totalAmount;

    @ApiModelProperty(value = "入库处理费")
    private BigDecimal warehouseFee;

    @ApiModelProperty(value = "出库处理费")
    private BigDecimal exWarehourseFee;

    @ApiModelProperty(value = "包材操作费")
    private BigDecimal packageFee;

    @ApiModelProperty(value = "运费")
    private BigDecimal freightFee;

    @ApiModelProperty(value = "处理费")
    private BigDecimal handerFee;

    @ApiModelProperty(value = "偏远附加费")
    private BigDecimal remoteFee;

    @ApiModelProperty(value = "燃油附加费")
    private BigDecimal fuelFee;

    @ApiModelProperty(value = "退件费")
    private BigDecimal returnFee;

    @ApiModelProperty(value = "包裹销毁费")
    private BigDecimal packageDestrueFee;

    @ApiModelProperty(value = "其他费")
    private BigDecimal otherFee;



}
