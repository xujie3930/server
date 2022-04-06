package com.szmsd.http.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-09 10:49
 */
@Data
@ApiModel(value = "CreateShipmentRequestDto", description = "CreateShipmentRequestDto对象")
public class CreateShipmentRequestDto implements Serializable {

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "出库订单类型")
    private String orderType;

    @ApiModelProperty(value = "卖家代码")
    private String sellerCode;

    @ApiModelProperty(value = "挂号（可以为空）")
    private String trackingNo;

    @ApiModelProperty(value = "发货规则（也就是物流承运商，必须填写指定值，例如Fedex, USPS等，相同代表一起交货。）")
    private String shipmentRule;

    @ApiModelProperty(value = "装箱规则（在发货规则相同的前提下，相同装箱规则代表可以一起装箱）可空")
    private String packingRule;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "参照单号")
    private String refOrderNo;

    @ApiModelProperty(value = "是否必须按要求装箱")
    private Boolean isPackingByRequired;

    @ApiModelProperty(value = "是否优先发货")
    private Boolean isFirst;

    @ApiModelProperty(value = "出库后重新上架的新SKU编码")
    private String newSKU;

    @ApiModelProperty(value = "出库单地址")
    private ShipmentAddressDto address;

    @ApiModelProperty(value = "出库单明细")
    private List<ShipmentDetailInfoDto> details;

    @ApiModelProperty(value = "装箱要求")
    private PackingRequirementInfoDto packingRequirement;

    @ApiModelProperty(value = "任务流程配置")
    private TaskConfigInfo taskConfig;

}
