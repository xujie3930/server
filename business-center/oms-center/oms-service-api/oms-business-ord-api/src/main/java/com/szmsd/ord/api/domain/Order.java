package com.szmsd.ord.api.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author lufei
 * @version 1.0
 * @Date 2020-06-10 10:36
 * @Description
 */
@Data
public class Order extends BaseEntity {

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "运单号")
    private String waybillNo;

    @ApiModelProperty(value = "订单状态:01=未处理，02=中心已调派，03=已调度")
    private String orderStatus;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "ORDER DATE")
    @ApiModelProperty(value = "下单日期")
    private Date orderDate;

    @ApiModelProperty(value = "客户编号")
    private String cusCode;

    @ApiModelProperty(value = "客户名称")
    private String cusName;

    @Excel(name = "CUSTOMER ORDER NUMBER")
    @ApiModelProperty(value = "客户订单号")
    private String cusOrderNo;

    @ApiModelProperty(value = "产品类型编号")
    private String productTypeCode;

    @Excel(name = "ORDER TYPE")
    @ApiModelProperty(value = "产品类型名称")
    private String productTypeName;

    @ApiModelProperty(value = "产品单号前缀")
    private String prefixNumber;

    @Excel(name = "NUMBER OF UNITS")
    @ApiModelProperty(value = "件数")
    private Integer pieces;

    @Excel(name = "WEIGHT")
    @ApiModelProperty(value = "重量(KG)")
    private BigDecimal weight;

    @Excel(name = "VOLUME WEIGHT")
    @ApiModelProperty(value = "体积重")
    private BigDecimal volumeWeight;

    @Excel(name = "VOLUME LENGTH")
    @ApiModelProperty(value = "长度")
    private BigDecimal length;

    @Excel(name = "VOLUME WIDTH")
    @ApiModelProperty(value = "宽度")
    private BigDecimal width;

    @Excel(name = "VOLUME HEIGHT")
    @ApiModelProperty(value = "高度")
    private BigDecimal height;

    @ApiModelProperty(value = "体积")
    private BigDecimal volumeNumber;

    @Excel(name = "PAYMENT METHOD")
    @ApiModelProperty(value = "代收货款")
    private BigDecimal cod;

    @Excel(name = "COLLECTION CURRENCY")
    @ApiModelProperty(value = "代收货款币种")
    private String codCurrency;

    @ApiModelProperty(value = "代收货款币种")
    private String codCurrencyName;

    @Excel(name = "REMARKS")
    @ApiModelProperty(value = "备注")
    private String remark;

    @Excel(name = "SHIPPER COMPANY")
    @ApiModelProperty(value = "寄件公司")
    private String senderCompany;

    @Excel(name = "SHIPPER")
    @ApiModelProperty(value = "寄件人")
    private String sender;

    @Excel(name = "SHIPPER MOBILE NUMBER")
    @ApiModelProperty(value = "寄件人手机")
    private String senderTel;

    @Excel(name = "SHIPPER NUMBER")
    @ApiModelProperty(value = "寄件人电话")
    private String senderPhone;

    @Excel(name = "SHIPPER ZIP CODE")
    @ApiModelProperty(value = "寄件人邮编")
    private String senderPostcode;

    @Excel(name = "SHIPPER E-MAIL")
    @ApiModelProperty(value = "寄件人邮箱")
    private String senderEmail;

    @Excel(name = "SHIPPER  COUNTRY")
    @ApiModelProperty(value = "寄件人国家")
    private String senderCountry;

    @Excel(name = "SHIPPER PROVINCE")
    @ApiModelProperty(value = "寄件人省份")
    private String senderProvince;

    @Excel(name = "SHIPPER TOWN")
    @ApiModelProperty(value = "寄件人城市")
    private String senderCity;

    @Excel(name = "SHIPPER AREA")
    @ApiModelProperty(value = "寄件人区县")
    private String senderTown;

    @Excel(name = "SHIPPER ADDRESS")
    @ApiModelProperty(value = "寄件人详细地址")
    private String senderAddress;

    @ApiModelProperty(value = "取货网点编号")
    private String pickUpSiteCode;

    @ApiModelProperty(value = "取货网点名称")
    private String pickUpSiteName;

    @ApiModelProperty(value = "取货网点业务员编号")
    private String pickUpEmployeeCode;

    @ApiModelProperty(value = "取货网点业务员名称")
    private String pickUpEmployeeName;

    @Excel(name = "CONSIGNEE COMPANY")
    @ApiModelProperty(value = "收件公司")
    private String receiverCompany;

    @Excel(name = "CONSIGNEE")
    @ApiModelProperty(value = "收件人")
    private String receiver;

    @Excel(name = "CONSIGNEE MOBILE NUMBER")
    @ApiModelProperty(value = "收件人手机")
    private String receiverTel;

    @Excel(name = "CONSIGNEE NUMBER")
    @ApiModelProperty(value = "收件人电话")
    private String receiverPhone;

    @Excel(name = "CONSIGNEE ZIP CODE")
    @ApiModelProperty(value = "收件人邮编")
    private String receiverPostcode;

    @Excel(name = "CONSIGNEE E-MAIL")
    @ApiModelProperty(value = "收件人邮箱")
    private String receiverEmail;

    @Excel(name = "CONSIGNEE COUNTRY")
    @ApiModelProperty(value = "收件人国家")
    private String receiverCountry;

    @Excel(name = "CONSIGNEE PROVINCE")
    @ApiModelProperty(value = "收件人省份")
    private String receiverProvince;

    @Excel(name = "CONSIGNEE TOWN")
    @ApiModelProperty(value = "收件人城市")
    private String receiverCity;

    @Excel(name = "CONSIGNEE AREA")
    @ApiModelProperty(value = "收件人区县")
    private String receiverTown;

    @Excel(name = "CONSIGNEE ADDRESS")
    @ApiModelProperty(value = "收件人地址")
    private String receiverAddress;

    @ApiModelProperty(value = "目的网点编号")
    private String destSiteCode;

    @ApiModelProperty(value = "目的网点名称")
    private String destSiteName;

    @ApiModelProperty(value = "创建人名称")
    private String createBy;

    @ApiModelProperty(value = "最后修改人名称")
    private String updateBy;

    @ApiModelProperty(value = "删除状态:0已删除 1未删除")
    private int deletedStatus;

    @ApiModelProperty(value = "调派网点编号")
    private String assignSiteCode;

    @ApiModelProperty(value = "调派网点名称")
    private String assignSiteName;

    @ApiModelProperty(value = "调派人ID")
    private String assignerId;

    @ApiModelProperty(value = "调派人名称")
    private String assignerName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "调派时间")
    private Date assignTime;

    @ApiModelProperty(value = "重新调派网点编号")
    private String reassignSiteCode;

    @ApiModelProperty(value = "重新调派网点名称")
    private String reassignSiteName;

    @ApiModelProperty(value = "重新调派人ID")
    private String reassignerId;

    @ApiModelProperty(value = "重新调派人名称")
    private String reassignerName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "重新调派时间")
    private Date reassignTime;

    @ApiModelProperty(value = "重新调派原因")
    private String reassignReason;

    @ApiModelProperty(value = "调度人ID")
    private String dispatcherId;

    @ApiModelProperty(value = "调度人名称")
    private String dispatcherName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "调度时间")
    private Date dispatchTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "取件时间")
    private Date pickUpTime;

    @ApiModelProperty(value = "订单来源")
    private String orderSource;

    private List<ChildOrder> childOrders;

    @ApiModelProperty(value = "寄件人关键字id")
    private String senderKeywordCode;

    @ApiModelProperty(value = "寄件人关键字")
    private String senderKeywordName;

    @ApiModelProperty(value = "收件人关键字id")
    private String receiverKeywordCode;

    @ApiModelProperty(value = "收件人关键字")
    private String receiverKeywordName;

    @ApiModelProperty(value = "付款方式code")
    private String payMethodCode;

    @ApiModelProperty(value = "付款方式")
    private String payMethodName;

    @ApiModelProperty(value = "物品类型")
    private String itemType;

    @ApiModelProperty(value = "物品类型code")
    private String itemTypeCode;

    @ApiModelProperty(value = "纯运费")
    private BigDecimal netFreight;

    @ApiModelProperty(value = "总运费")
    private BigDecimal totalFreight;

    @ApiModelProperty(value = "品名")
    private String proName;

    @ApiModelProperty(value = "代收货款手续费")
    private BigDecimal payment;

    @ApiModelProperty(value = "异常内容")
    @Excel(name = "异常内容")
    private String msgContent;

    @ApiModelProperty(value = "清关状态编码  NONE-未申报，ING - 申报中，SUCCESS-申报成功，ERROR-申报失败")
    @Excel(name = "清关状态编码  NONE-未申报，ING - 申报中，SUCCESS-申报成功，ERROR-申报失败")
    private String clearCustomerStatusCode;

    @ApiModelProperty(value = "清关状态名称")
    @Excel(name = "清关状态名称")
    private String clearCustomerStatusName;

    @ApiModelProperty(value = "拒单原因")
    @Excel(name = "拒单原因")
    private String reasonsForCancellation;

    @ApiModelProperty(value = "二派标识")
    @Excel(name = "二派标识")
    private String twoIden;

    @ApiModelProperty(value = "二派费用")
    @Excel(name = "二派费用")
    private String twoCost;
}
