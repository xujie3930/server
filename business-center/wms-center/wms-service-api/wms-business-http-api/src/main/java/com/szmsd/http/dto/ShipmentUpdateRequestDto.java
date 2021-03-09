package com.szmsd.http.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-03-09 11:16
 */
@Data
@ApiModel(value = "ShipmentUpdateRequestDto", description = "ShipmentUpdateRequestDto对象")
public class ShipmentUpdateRequestDto implements Serializable {

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "OMS出库单号")
    private String refOrderNo;

    @ApiModelProperty(value = "发货规则（一般填写物流服务）")
    private String shipmentRule;

    @ApiModelProperty(value = "装箱规则（有需要仓库分箱的就填写，可空）")
    private String packingRule;

    @ApiModelProperty(value = "是否异常")
    private Boolean isEx;

    @ApiModelProperty(value = "异常类型")
    private String exType;

    @ApiModelProperty(value = "异常原因")
    private String exRemark;

    @ApiModelProperty(value = "是否需要贴运单标签")
    private Boolean isNeedShipmentLabel;
}
