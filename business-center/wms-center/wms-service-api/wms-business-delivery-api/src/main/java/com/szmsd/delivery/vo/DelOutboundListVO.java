package com.szmsd.delivery.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.szmsd.bas.plugin.AutoFieldValue;
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

    @AutoFieldValue(code = "063", valueField = "subValue")
    @ApiModelProperty(value = "出库订单类型")
    private String orderType;

    @ApiModelProperty(value = "出库订单类型名称")
    private String orderTypeName;

    @AutoFieldValue(code = "065", valueField = "subValue")
    @ApiModelProperty(value = "单据状态")
    private String state;

    @ApiModelProperty(value = "单据状态名称")
    private String stateName;

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

    @ApiModelProperty(value = "计费重")
    private Double billingWeight;

    @ApiModelProperty(value = "费用")
    private BigDecimal amount;

    @ApiModelProperty(value = "异常描述")
    private String exceptionMessage;

    @ApiModelProperty(value = "客户代码")
    private String customCode;

    @ApiModelProperty(value = "创建人")
    private String createByName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "收件人")
    private String consignee;

}
