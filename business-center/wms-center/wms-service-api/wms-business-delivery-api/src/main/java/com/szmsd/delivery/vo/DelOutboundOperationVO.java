package com.szmsd.delivery.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "DelOutboundOperationVO", description = "DelOutboundOperationVO对象")
public class DelOutboundOperationVO implements Serializable {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "出库单号")
    private String orderNo;

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;
    /**
     * {@link com.szmsd.chargerules.enums.DelOutboundOrderEnum}
     */
    @ApiModelProperty(value = "出库订单类型")
    private String orderType;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "客户代码")
    private String customCode;

    @ApiModelProperty(value = "单据状态")
    private String state;

    @ApiModelProperty(value = "重量 g")
    private Double weight;

    @ApiModelProperty(value = "批量出库贴标数量")
    private Integer shipmentLabelCount;

    @ApiModelProperty(value = "装箱数量")
    private Integer packingCount;

    @ApiModelProperty(value = "明细信息")
    private List<DelOutboundOperationDetailVO> details;

}
