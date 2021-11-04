package com.szmsd.delivery.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.szmsd.bas.plugin.BasSubCommonPlugin;
import com.szmsd.bas.plugin.BasSubValueCommonParameter;
import com.szmsd.common.plugin.annotation.AutoFieldValue;
import com.szmsd.common.plugin.interfaces.DefaultCommonParameter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zhangyuyuan
 * @date 2021-03-05 14:21
 */
@Data
@ApiModel(value = "DelOutboundListVO", description = "DelOutboundListVO对象")
public class DelOutboundListVO implements Serializable {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "出库单号")
    private String orderNo;

    @ApiModelProperty(value = "采购单号")
    private String purchaseNo;

    @AutoFieldValue(supports = BasSubCommonPlugin.SUPPORTS, code = "063", cp = BasSubValueCommonParameter.class)
    @ApiModelProperty(value = "出库订单类型")
    private String orderType;

    @ApiModelProperty(value = "出库订单类型名称")
    private String orderTypeName;

    @AutoFieldValue(supports = BasSubCommonPlugin.SUPPORTS, code = "065", cp = BasSubValueCommonParameter.class)
    @ApiModelProperty(value = "单据状态")
    private String state;

    @ApiModelProperty(value = "单据状态名称")
    private String stateName;

    @AutoFieldValue(supports = "BasWarehouse", cp = DefaultCommonParameter.class, nameField = "warehouseName")
    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "仓库名称")
    private String warehouseName;

    @ApiModelProperty(value = "发货规则（也就是物流承运商，必须填写指定值，例如Fedex, USPS等，相同代表一起交货。）")
    private String shipmentRule;

    @ApiModelProperty(value = "挂号")
    private String trackingNo;

    @ApiModelProperty(value = "规格")
    private String specifications;

    @ApiModelProperty(value = "重量")
    private Double weight;

    @ApiModelProperty(value = "计费重")
    private Double calcWeight;

    @ApiModelProperty(value = "计费单位")
    private String calcWeightUnit;

    @ApiModelProperty(value = "费用")
    private BigDecimal amount;

    @ApiModelProperty(value = "币种")
    private String currencyCode;

    @AutoFieldValue(supports = BasSubCommonPlugin.SUPPORTS, code = "066", cp = BasSubValueCommonParameter.class)
    @ApiModelProperty(value = "异常状态")
    private String exceptionState;

    @ApiModelProperty(value = "异常状态名称")
    private String exceptionStateName;

    @ApiModelProperty(value = "异常描述")
    private String exceptionMessage;

    @ApiModelProperty(value = "客户代码")
    private String customCode;

    @ApiModelProperty(value = "创建人")
    private String createByName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "收件人")
    private String consignee;

    @ApiModelProperty(value = "电话号码")
    private String phoneNo;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "街道1")
    private String street1;

    @ApiModelProperty(value = "街道2")
    private String street2;

    @ApiModelProperty(value = "街道3")
    private String street3;

    @ApiModelProperty(value = "城市")
    private String city;

    @ApiModelProperty(value = "省份/州")
    private String stateOrProvince;

    @ApiModelProperty(value = "国家代码")
    private String countryCode;

    @ApiModelProperty(value = "国家名称")
    private String country;

    @ApiModelProperty(value = "邮编")
    private String postCode;

    @ApiModelProperty(value = "是否打印标签")
    private Boolean isPrint;

    @ApiModelProperty(value = "是否贴箱标")
    private Boolean isLabelBox;
}
