package com.szmsd.delivery.dto;

import com.szmsd.common.core.validator.ValidationUpdateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-05 14:21
 */
@Data
@ApiModel(value = "DelOutboundDto", description = "DelOutboundDto对象")
public class DelOutboundDto implements Serializable {

    @NotNull(message = "ID不能为空", groups = ValidationUpdateGroup.class)
    @ApiModelProperty(value = "ID")
    private Long id;

    @NotBlank(message = "仓库代码不能为空")
    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @NotBlank(message = "出库单类型不能为空")
    @ApiModelProperty(value = "出库订单类型")
    private String orderType;

    @NotBlank(message = "卖家代码不能为空")
    @ApiModelProperty(value = "卖家代码")
    private String sellerCode;

    @ApiModelProperty(value = "挂号")
    private String trackingNo;

    @NotBlank(message = "发货规则不能为空")
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

    @NotNull(message = "地址信息不能为空")
    @ApiModelProperty(value = "地址信息")
    private DelOutboundAddressDto address;

    @NotNull(message = "明细信息不能为空")
    @ApiModelProperty(value = "明细信息")
    private List<DelOutboundDetailDto> details;
}
