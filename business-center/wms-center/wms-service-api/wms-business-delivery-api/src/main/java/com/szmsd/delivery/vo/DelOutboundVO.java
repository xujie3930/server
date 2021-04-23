package com.szmsd.delivery.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-05 14:21
 */
@Data
@ApiModel(value = "DelOutboundVO", description = "DelOutboundVO对象")
public class DelOutboundVO implements Serializable {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "出库单号")
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

    @ApiModelProperty(value = "装箱规则")
    private String packingRule;

    @ApiModelProperty(value = "备注")
    private String remark;

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

    @ApiModelProperty(value = "提货方式")
    private String deliveryMethod;

    @ApiModelProperty(value = "提货时间")
    private String deliveryTime;

    @ApiModelProperty(value = "提货商/快递商")
    private String deliveryAgent;

    @ApiModelProperty(value = "提货/快递信息")
    private String deliveryInfo;

    @ApiModelProperty(value = "包裹重量尺寸确认")
    private String packageConfirm;

    @ApiModelProperty(value = "包裹重量误差")
    private Integer packageWeightDeviation;

    @ApiModelProperty(value = "长 CM")
    private Double length;

    @ApiModelProperty(value = "宽 CM")
    private Double width;

    @ApiModelProperty(value = "高 CM")
    private Double height;

    @ApiModelProperty(value = "重量 g")
    private Double weight;

    @ApiModelProperty(value = "地址信息")
    private DelOutboundAddressVO address;

    @ApiModelProperty(value = "明细信息")
    private List<DelOutboundDetailVO> details;

}
