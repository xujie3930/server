package com.szmsd.delivery.dto;

import com.szmsd.bas.api.domain.dto.AttachmentDataDTO;
import com.szmsd.common.core.validator.ValidationSaveGroup;
import com.szmsd.common.core.validator.ValidationUpdateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
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

    @NotBlank(message = "客户代码不能为空")
    @ApiModelProperty(value = "客户代码")
    private String customCode;

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

    @ApiModelProperty(value = "提货方式")
    private String deliveryMethod;

    @ApiModelProperty(value = "提货时间")
    private Date deliveryTime;

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

    @ApiModelProperty(value = "出货渠道")
    private String shipmentChannel;

    @ApiModelProperty(value = "是否默认仓库装箱数据")
    private Boolean isDefaultWarehouse;

    @ApiModelProperty(value = "是否贴箱标")
    private Boolean isLabelBox;

    @ApiModelProperty(value = "装箱数量")
    private Long boxNumber;

    @ApiModelProperty(value = "来源")
    private String sourceType;

    @ApiModelProperty(value = "增值税号")
    private String ioss;

    @ApiModelProperty(value = "COD")
    private BigDecimal codAmount;

    @ApiModelProperty(value = "重派")
    private String reassignType;

    @ApiModelProperty(value = "原单号")
    private String oldOrderNo;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "shopify订单号")
    private String shopifyOrderNo;

    @ApiModelProperty(value = "伙伴编码")
    private String partnerCode;

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


    @NotNull(message = "地址信息不能为空")
    @ApiModelProperty(value = "地址信息")
    private DelOutboundAddressDto address;

    @NotNull(message = "明细信息不能为空", groups = {ValidationSaveGroup.class, ValidationUpdateGroup.class})
    @ApiModelProperty(value = "明细信息")
    private List<DelOutboundDetailDto> details;

    @ApiModelProperty(value = "文件信息")
    private List<AttachmentDataDTO> documentsFiles;

    @ApiModelProperty(value = "箱标文件")
    private List<AttachmentDataDTO> batchLabels;

    @ApiModelProperty(value = "装箱信息")
    private List<DelOutboundPackingDto> packings;

    @ApiModelProperty(value = "组合信息")
    private List<DelOutboundCombinationDto> combinations;


    @ApiModelProperty(value = "亚马逊物流服务id")
    private String amazonLogisticsRouteId;


}
