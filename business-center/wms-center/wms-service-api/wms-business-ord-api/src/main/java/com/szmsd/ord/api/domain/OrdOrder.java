package com.szmsd.ord.api.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * <p>
 * 订单表
 * </p>
 *
 * @author ziling
 * @since 2020-07-28
 */
@TableName("ord_order")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@SuppressWarnings("serial")
@Data
@ApiModel(value = "OrdOrder对象", description = "订单表")
public class OrdOrder {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "订单号")
    @Excel(name = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "运单号")
    @Excel(name = "运单号")
    private String waybillNo;

    @ApiModelProperty(value = "订单状态")
    @Excel(name = "订单状态")
    private String orderStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "下单日期")
    @Excel(name = "下单日期")
    private Date orderDate;

    @ApiModelProperty(value = "客户编号")
    @Excel(name = "客户编号")
    private String cusCode;

    @ApiModelProperty(value = "客户名称")
    @Excel(name = "客户名称")
    private String cusName;

    @ApiModelProperty(value = "客户订单号")
    @Excel(name = "客户订单号")
    private String cusOrderNo;

    @ApiModelProperty(value = "产品类型编码")
    @Excel(name = "产品类型编码")
    private String productTypeCode;

    @ApiModelProperty(value = "产品类型名称")
    @Excel(name = "产品类型名称")
    private String productTypeName;

    @ApiModelProperty(value = "件数")
    @Excel(name = "件数")
    private Integer pieces;

    @ApiModelProperty(value = "重量")
    @Excel(name = "重量")
    private BigDecimal weight;

    @ApiModelProperty(value = "体积重")
    @Excel(name = "体积重")
    private BigDecimal volumeWeight;

    @ApiModelProperty(value = "长度，单位厘米")
    @Excel(name = "长度，单位厘米")
    private BigDecimal length;

    @ApiModelProperty(value = "宽度，单位厘米")
    @Excel(name = "宽度，单位厘米")
    private BigDecimal width;

    @ApiModelProperty(value = "高度，单位厘米")
    @Excel(name = "高度，单位厘米")
    private BigDecimal height;

    @ApiModelProperty(value = "体积，单位立方米")
    @Excel(name = "体积，单位立方米")
    private BigDecimal volumeNumber;

    @ApiModelProperty(value = "代收货款")
    @Excel(name = "代收货款")
    private BigDecimal cod;

    @ApiModelProperty(value = "代收货款币种code")
    @Excel(name = "代收货款币种code")
    private String codCurrency;

    @ApiModelProperty(value = "代收货款币种")
    @Excel(name = "代收货款币种")
    private String codCurrencyName;

    @ApiModelProperty(value = "寄件公司")
    @Excel(name = "寄件公司")
    private String senderCompany;

    @ApiModelProperty(value = "寄件人")
    @Excel(name = "寄件人")
    private String sender;

    @ApiModelProperty(value = "寄件人手机")
    @Excel(name = "寄件人手机")
    private String senderTel;

    @ApiModelProperty(value = "寄件人电话")
    @Excel(name = "寄件人电话")
    private String senderPhone;

    @ApiModelProperty(value = "寄件人邮编")
    @Excel(name = "寄件人邮编")
    private String senderPostcode;

    @ApiModelProperty(value = "寄件人邮箱")
    @Excel(name = "寄件人邮箱")
    private String senderEmail;

    @ApiModelProperty(value = "寄件人国家code")
    @Excel(name = "寄件人国家code")
    private String senderCountryCode;

    @ApiModelProperty(value = "寄件人国家中")
    @Excel(name = "寄件人国家中")
    private String senderCountry;

    @ApiModelProperty(value = "寄件人国家英")
    @Excel(name = "寄件人国家英")
    private String senderCountryEn;

    @ApiModelProperty(value = "寄件人国家阿拉伯")
    @Excel(name = "寄件人国家阿拉伯")
    private String senderCountryAr;

    @ApiModelProperty(value = "寄件人省份")
    @Excel(name = "寄件人省份")
    private String senderProvince;

    @ApiModelProperty(value = "寄件人城市")
    @Excel(name = "寄件人城市")
    private String senderCity;

    @ApiModelProperty(value = "寄件人区县")
    @Excel(name = "寄件人区县")
    private String senderTown;

    @ApiModelProperty(value = "寄件人详细地址")
    @Excel(name = "寄件人详细地址")
    private String senderAddress;

    @ApiModelProperty(value = "取货网点编号")
    @Excel(name = "取货网点编号")
    private String pickUpSiteCode;

    @ApiModelProperty(value = "取货网点名称")
    @Excel(name = "取货网点名称")
    private String pickUpSiteName;

    @ApiModelProperty(value = "取货业务员编号")
    @Excel(name = "取货业务员编号")
    private String pickUpEmployeeCode;

    @ApiModelProperty(value = "取货业务员名称")
    @Excel(name = "取货业务员名称")
    private String pickUpEmployeeName;

    @ApiModelProperty(value = "收件公司")
    @Excel(name = "收件公司")
    private String receiverCompany;

    @ApiModelProperty(value = "收件人")
    @Excel(name = "收件人")
    private String receiver;

    @ApiModelProperty(value = "收件人手机")
    @Excel(name = "收件人手机")
    private String receiverTel;

    @ApiModelProperty(value = "收件人电话")
    @Excel(name = "收件人电话")
    private String receiverPhone;

    @ApiModelProperty(value = "收件人邮编")
    @Excel(name = "收件人邮编")
    private String receiverPostcode;

    @ApiModelProperty(value = "收件人邮箱")
    @Excel(name = "收件人邮箱")
    private String receiverEmail;

    @ApiModelProperty(value = "收件人国家code")
    @Excel(name = "收件人国家code")
    private String receiverCountryCode;

    @ApiModelProperty(value = "收件人国家英")
    @Excel(name = "收件人国家英")
    private String receiverCountryEn;

    @ApiModelProperty(value = "收件人国家阿拉伯")
    @Excel(name = "收件人国家阿拉伯")
    private String receiverCountryAr;

    @ApiModelProperty(value = "收件人国家")
    @Excel(name = "收件人国家")
    private String receiverCountry;

    @ApiModelProperty(value = "收件人省份")
    @Excel(name = "收件人省份")
    private String receiverProvince;

    @ApiModelProperty(value = "收件人城市")
    @Excel(name = "收件人城市")
    private String receiverCity;

    @ApiModelProperty(value = "收件人区县")
    @Excel(name = "收件人区县")
    private String receiverTown;

    @ApiModelProperty(value = "收件人地址")
    @Excel(name = "收件人地址")
    private String receiverAddress;

    @ApiModelProperty(value = "目的网点编号")
    @Excel(name = "目的网点编号")
    private String destSiteCode;

    @ApiModelProperty(value = "目的网点名称")
    @Excel(name = "目的网点名称")
    private String destSiteName;

    @ApiModelProperty(value = "创建人")
    @Excel(name = "创建人")
    private String createBy;

    @ApiModelProperty(value = "创建人名称")
    @Excel(name = "创建人名称")
    private String createByName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建人时间")
    @Excel(name = "创建人时间")
    private Date createTime;

    @ApiModelProperty(value = "最后修改人")
    @Excel(name = "最后修改人")
    private String updateBy;

    @ApiModelProperty(value = "最后修改人名称")
    @Excel(name = "最后修改人名称")
    private String updateByName;

    @ApiModelProperty(value = "最后修改时间")
    @Excel(name = "最后修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "删除状态：0删除 1未删除")
    @Excel(name = "删除状态：0删除 1未删除")
    private String deletedStatus;

    @ApiModelProperty(value = "预留字段")
    @Excel(name = "预留字段")
    private String parm1;

    @ApiModelProperty(value = "预留字段")
    @Excel(name = "预留字段")
    private String parm2;

    @ApiModelProperty(value = "预留字段")
    @Excel(name = "预留字段")
    private String parm3;

    @ApiModelProperty(value = "预留字段")
    @Excel(name = "预留字段")
    private String parm4;

    @ApiModelProperty(value = "预留字段")
    @Excel(name = "预留字段")
    private String parm5;

    @ApiModelProperty(value = "调派网点编号")
    @Excel(name = "调派网点编号")
    private String assignSiteCode;

    @ApiModelProperty(value = "调派网点名称")
    @Excel(name = "调派网点名称")
    private String assignSiteName;

    @ApiModelProperty(value = "调派人ID")
    @Excel(name = "调派人ID")
    private String assignerId;

    @ApiModelProperty(value = "调派人名称")
    @Excel(name = "调派人名称")
    private String assignerName;

    @ApiModelProperty(value = "调派时间")
    @Excel(name = "调派时间")
    private Date assignTime;

    @ApiModelProperty(value = "重新调派网点编号")
    @Excel(name = "重新调派网点编号")
    private String reassignSiteCode;

    @ApiModelProperty(value = "重新调派网点名称")
    @Excel(name = "重新调派网点名称")
    private String reassignSiteName;

    @ApiModelProperty(value = "重新调派人ID")
    @Excel(name = "重新调派人ID")
    private String reassignerId;

    @ApiModelProperty(value = "重新调派人名称")
    @Excel(name = "重新调派人名称")
    private String reassignerName;

    @ApiModelProperty(value = "重新调派时间")
    @Excel(name = "重新调派时间")
    private Date reassignTime;

    @ApiModelProperty(value = "重新调派原因")
    @Excel(name = "重新调派原因")
    private String reassignReason;

    @ApiModelProperty(value = "调度人ID")
    @Excel(name = "调度人ID")
    private String dispatcherId;

    @ApiModelProperty(value = "调度人名称")
    @Excel(name = "调度人名称")
    private String dispatcherName;

    @ApiModelProperty(value = "调度时间")
    @Excel(name = "调度时间")
    private Date dispatchTime;

    @ApiModelProperty(value = "取件时间")
    @Excel(name = "取件时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date pickUpTime;

    @ApiModelProperty(value = "订单来源")
    @Excel(name = "订单来源")
    private String orderSource;

    @ApiModelProperty(value = "接单时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    @Excel(name = "接单时间")
    private Date orderTaskingDate;

    @TableField(exist = false)
    @ApiModelProperty(value = "单号前缀")
    @Excel(name = "单号前缀")
    private String prefixNumber;

    @ApiModelProperty(value = "寄件人关键字id")
    @Excel(name = "寄件人关键字id")
    private String senderKeywordCode;

    @ApiModelProperty(value = "寄件人关键字")
    @Excel(name = "寄件人关键字")
    private String senderKeywordName;

    @ApiModelProperty(value = "收件人关键字id")
    @Excel(name = "收件人关键字id")
    private String receiverKeywordCode;

    @ApiModelProperty(value = "收件人关键字")
    @Excel(name = "收件人关键字")
    private String receiverKeywordName;

    @TableField(exist = false)
    @ApiModelProperty(value = "开始时间")
    @Excel(name = "开始时间")
    private String startTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "结束时间")
    @Excel(name = "结束时间")
    private String endTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "错误信息")
    @Excel(name = "错误信息")
    private String message;

    @ApiModelProperty(value = "自动调派标识0 =手动调派 1=自动调派")
    @Excel(name = "自动调派标识")
    private String iden;

    @ApiModelProperty(value = "目的地")
    private String destinationName;

    @ApiModelProperty(value = "目的地编码")
    private String destinationCode;

    @ApiModelProperty(value = "保价金额")
    @Excel(name = "保价金额")
    private BigDecimal insuredAmount;

    @ApiModelProperty(value = "备注")
    @Excel(name = "备注")
    private String remark;

    @ApiModelProperty(value = "付款方式code")
    @Excel(name = "付款方式code")
    private String payMethodCode;

    @ApiModelProperty(value = "付款方式")
    @Excel(name = "付款方式")
    private String payMethodName;

    @ApiModelProperty(value = "所有者pob")
    @Excel(name = "所有者pob")
    private String ownerPobOxNo;

    @ApiModelProperty(value = "进口商编号")
    @Excel(name = "进口商编号")
    private String importerNumber;

    @ApiModelProperty(value = "进口商英文")
    @Excel(name = "进口商英文")
    private String importNameEng;

    @ApiModelProperty(value = "进口商名称阿拉伯语")
    @Excel(name = "进口商名称阿拉伯语")
    private String importerNameArb;

    @ApiModelProperty(value = "进口商电话")
    @Excel(name = "进口商电话")
    private String importerPhoneNo;

    @ApiModelProperty(value = "进口商传真")
    @Excel(name = "进口商传真")
    private String importerFaxNo;

    @ApiModelProperty(value = "航班号")
    @Excel(name = "航班号")
    private String flightNumber;

    @ApiModelProperty(value = "航班出发日期")
    @Excel(name = "航班出发日期")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date flightDepartureDate;

    @ApiModelProperty(value = "航班到达日期")
    @Excel(name = "航班到达日期")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date flightArrivalDate;

    @ApiModelProperty(value = "卸货日期")
    @Excel(name = "卸货日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date unloadDate;

    @ApiModelProperty(value = "AWB到货数量（提单）")
    @Excel(name = "AWB到货数量（提单）")
    private BigDecimal arrivedQuantity;

    @ApiModelProperty(value = "AWB到货重量（提单）")
    @Excel(name = "AWB到货重量（提单）")
    private BigDecimal arrivedWeight;

    @ApiModelProperty(value = "到达日期")
    @Excel(name = "到达日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date arrivedDate;

    @ApiModelProperty(value = "项目序列号")
    @Excel(name = "项目序列号")
    private String itemSeqNo;

    @ApiModelProperty(value = "单位 代码")
    @Excel(name = "单位 代码")
    private String packageTypeCode;

    @ApiModelProperty(value = "AWB到货重量（提单）")
    @Excel(name = "AWB到货重量（提单）")
    private BigDecimal itemQuantity;

    @ApiModelProperty(value = "产品重量")
    @Excel(name = "产品重量")
    private BigDecimal itemWeight;

    @ApiModelProperty(value = "英文描述")
    @Excel(name = "英文描述")
    private String itemEngDesc;

    @ApiModelProperty(value = "阿拉伯描述")
    @Excel(name = "阿拉伯描述")
    private String itemArbDesc;

    @ApiModelProperty(value = "申报价值")
    @Excel(name = "申报价值")
    private BigDecimal declaredValue;

    @ApiModelProperty(value = "物品标记")
    @Excel(name = "物品标记")
    private String itemMarks;

    @ApiModelProperty(value = "序列号")
    @Excel(name = "序列号")
    private String seqNo;

    @ApiModelProperty(value = "仓库编号")
    @Excel(name = "仓库编号")
    private String warehouseCode;

    @ApiModelProperty(value = "仓库数量")
    @Excel(name = "仓库数量")
    private String warehouseQuantity;

    @ApiModelProperty(value = "收货人阿拉伯文")
    @Excel(name = "收货人阿拉伯文")
    private String ownerArbName;

    @ApiModelProperty(value = "物品类型")
    @Excel(name = "物品类型")
    private String itemType;

    @ApiModelProperty(value = "物品类型code")
    @Excel(name = "物品类型code")
    private String itemTypeCode;

    @ApiModelProperty(value = "纯运费")
    @Excel(name = "纯运费")
    private BigDecimal netFreight;

    @ApiModelProperty(value = "总运费")
    @Excel(name = "总运费")
    private BigDecimal totalFreight;

    @ApiModelProperty(value = "品名")
    @Excel(name = "品名")
    private String proName;

    @ApiModelProperty(value = "代收货款手续费")
    @Excel(name = "代收货款手续费")
    private BigDecimal payment;

    @ApiModelProperty(value = "保价费")
    @Excel(name = "保价费")
    private BigDecimal premium;

    @ApiModelProperty(value = "计费重量")
    @Excel(name = "计费重量")
    private BigDecimal billingWeight;

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

    @ApiModelProperty(value = "调派状态 0未处理 1=调派 2=调度")
    @Excel(name = "调派状态")
    private String schedulingStatus;

    @ApiModelProperty(value = "订单号")
    @TableField(exist = false )
    private List<String> orderNoList;

    @ApiModelProperty(value = "清关费用")
    @Excel(name = "清关费用")
    private BigDecimal clearCharge;

}
