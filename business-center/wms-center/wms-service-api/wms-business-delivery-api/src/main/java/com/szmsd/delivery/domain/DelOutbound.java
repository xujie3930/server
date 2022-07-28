package com.szmsd.delivery.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;


/**
 * <p>
 * 出库单
 * </p>
 *
 * @author asd
 * @since 2021-03-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "出库单", description = "DelOutbound对象")
public class DelOutbound extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "创建人编号")
    private String createBy;

    @ApiModelProperty(value = "修改人编号")
    private String updateBy;

    @ApiModelProperty(value = "版本号")
    private Long version;

    @ApiModelProperty(value = "逻辑删除标识；2-已删除，0-未删除")
    private String delFlag;

    @ApiModelProperty(value = "跟踪ID")
    private String traceId;

    @ApiModelProperty(value = "单号")
    private String orderNo;

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "出库订单类型")
    private String orderType;

    @ApiModelProperty(value = "卖家代码")
    private String sellerCode;

    @ApiModelProperty(value = "挂号")
    private String trackingNo;

    @ApiModelProperty(value = "发货规则（也就是物流承运商，必须填写指定值，例如Fedex, USPS等，相同代表一起交货。）")
    private String shipmentRule;

    @ApiModelProperty(value = "发货服务名称")
    private String shipmentService;

    @ApiModelProperty(value = "装箱规则")
    private String packingRule;

    @ApiModelProperty(value = "refno")
    private String refNo;

    @ApiModelProperty(value = "参照单号")
    private String refOrderNo;

    @ApiModelProperty(value = "是否必须按要求装箱")
    private Boolean isPackingByRequired;

    @ApiModelProperty(value = "是否优先发货")
    private Boolean isFirst;

    @ApiModelProperty(value = "出库后重新上架的新SKU编码")
    private String newSku;

    @ApiModelProperty(value = "客户代码")
    private String customCode;

    @ApiModelProperty(value = "单据状态")
    private String state;

    @ApiModelProperty(value = "长 CM")
    private Double length;

    @ApiModelProperty(value = "宽 CM")
    private Double width;

    @ApiModelProperty(value = "高 CM")
    private Double height;

    @ApiModelProperty(value = "重量 g")
    private Double weight;

    @ApiModelProperty(value = "规格")
    private String specifications;

    @ApiModelProperty(value = "包材类型")
    private String packingMaterial;

    @ApiModelProperty(value = "操作类型，开始处理：Processing，已发货：Shipped，已取消：Canceled")
    private String operationType;

    @ApiModelProperty(value = "操作时间")
    private Date operationTime;

    @ApiModelProperty(value = "采购单号")
    private String purchaseNo;

    @ApiModelProperty(value = "计费重")
    private BigDecimal calcWeight;

    @ApiModelProperty(value = "计费重单位")
    private String calcWeightUnit;

    @ApiModelProperty(value = "金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "币种")
    private String currencyCode;

    @ApiModelProperty(value = "异常状态")
    private String exceptionState;

    @ApiModelProperty(value = "异常描述")
    private String exceptionMessage;

    @ApiModelProperty(value = "发货类型")
    private String shipmentType;

    @ApiModelProperty(value = "提货方式")
    private String deliveryMethod;

    @ApiModelProperty(value = "提货时间")
    private Date deliveryTime;

    @ApiModelProperty(value = "提货商/快递商")
    private String deliveryAgent;

    @ApiModelProperty(value = "提货/快递信息")
    private String deliveryInfo;

    @ApiModelProperty(value = "挂号获取方式")
    private String trackingAcquireType;

    @ApiModelProperty(value = "承运商订单号")
    private String shipmentOrderNumber;

    @ApiModelProperty(value = "预审状态")
    private String bringVerifyState;

    @ApiModelProperty(value = "出库状态")
    private String shipmentState;

    @ApiModelProperty(value = "单据完成处理状态")
    private String completedState;

    @ApiModelProperty(value = "单据取消处理状态")
    private String cancelledState;

    @ApiModelProperty(value = "包裹重量尺寸确认")
    private String packageConfirm;

    @ApiModelProperty(value = "包裹重量误差")
    private Long packageWeightDeviation;

    @ApiModelProperty(value = "提审时间")
    private Date bringVerifyTime;

    @ApiModelProperty(value = "到仓时间")
    private Date arrivalTime;

    @ApiModelProperty(value = "发货时间")
    private Date shipmentsTime;

    @ApiModelProperty(value = "箱数")
    private Long boxNumber;

    @ApiModelProperty(value = "出货渠道")
    private String shipmentChannel;

    @ApiModelProperty(value = "装箱状态")
    private Integer containerState;

    @ApiModelProperty(value = "是否默认仓库装箱数据")
    private Boolean isDefaultWarehouse;

    @ApiModelProperty(value = "是否贴箱标")
    private Boolean isLabelBox;

    @ApiModelProperty(value = "来源")
    private String sourceType;

    @ApiModelProperty(value = "是否打印标签")
    private Boolean isPrint;

    @ApiModelProperty(value = "增值税号")
    private String ioss;

    @ApiModelProperty(value = "COD")
    private BigDecimal codAmount;

    @ApiModelProperty(value = "承运商标签路径")
    private String shipmentOrderLabelUrl;

    @ApiModelProperty(value = "产品详情返回的发货规则")
    private String productShipmentRule;

    @ApiModelProperty(value = "重派")
    private String reassignType;

    @ApiModelProperty(value = "原单号")
    private String oldOrderNo;

    @ApiModelProperty(value = "物流商code")
    private String logisticsProviderCode;

    @ApiModelProperty(value = "TY运输包裹ID")
    private String tyShipmentId;

    @ApiModelProperty(value = "轨迹状态")
    private String trackingStatus;

    @ApiModelProperty(value = "轨迹信息描述")
    private String trackingDescription;

    @ApiModelProperty(value = "最新轨迹时间")
    private Date trackingTime;

    @ApiModelProperty(value = "供应商计价类型")
    private String supplierCalcType;

    @ApiModelProperty(value = "供应商计价id（线路图id或者物流服务id）")
    private String supplierCalcId;

    @ApiModelProperty(value = "shopify订单号")
    private String shopifyOrderNo;

    @ApiModelProperty(value = "伙伴编码")
    private String partnerCode;

    @ApiModelProperty(value = "临时字段")
    @TableField(exist = false)
    private String prcProductCode;

    @ApiModelProperty(value = "转运出库标签图片")
    private String shipmentRetryLabel;

    @ApiModelProperty(value = "是否上传箱标")
    private String uploadBoxLabel;

    @ApiModelProperty(value = "是否入库")
    private Boolean inStock;


    @ApiModelProperty(value = "清关方式")
    private String customsClearanceMethod;

    @ApiModelProperty(value = "VAT")
    private String vat;

    @ApiModelProperty(value = "客户单号")
    private String customerOrderNo;

    @ApiModelProperty(value = "Amazon Reference ID")
    private String amazonReferenceId;


    @ApiModelProperty(value = "送达目的地")
    private String destinationDelivery;

    @ApiModelProperty(value = "送达目的地名称")
    private String destinationDeliveryName;


    @ApiModelProperty(value = "仓库选择")
    private String warehouseSelection;


    @ApiModelProperty(value = "订单推送状态(大货) null 未推送 1.已推送wms 2.推送失败")
    private String pushOrderState;
}
