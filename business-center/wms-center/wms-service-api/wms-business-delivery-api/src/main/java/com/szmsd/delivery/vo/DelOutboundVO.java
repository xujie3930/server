package com.szmsd.delivery.vo;

import com.szmsd.bas.api.domain.dto.AttachmentDataDTO;
import com.szmsd.bas.plugin.BasSubCommonPlugin;
import com.szmsd.bas.plugin.BasSubValueCommonParameter;
import com.szmsd.common.plugin.annotation.AutoFieldValue;
import com.szmsd.common.plugin.interfaces.DefaultCommonParameter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
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

    @AutoFieldValue(supports = "BasWarehouse", cp = DefaultCommonParameter.class, nameField = "warehouseName")
    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "仓库名称")
    private String warehouseName;

    @AutoFieldValue(supports = BasSubCommonPlugin.SUPPORTS, code = "063", cp = BasSubValueCommonParameter.class)
    @ApiModelProperty(value = "出库订单类型")
    private String orderType;

    @ApiModelProperty(value = "出库订单类型名称")
    private String orderTypeName;

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
    @AutoFieldValue(supports = "BasProduct", cp = DefaultCommonParameter.class, nameField = "newSkuName")
    private String newSku;

    @ApiModelProperty(value = "出库后重新上架的新SKU编码名称")
    private String newSkuName;

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

    @ApiModelProperty(value = "规格")
    private String specifications;

    @ApiModelProperty(value = "出货渠道")
    private String shipmentChannel;

    @ApiModelProperty(value = "是否默认仓库装箱数据")
    private Boolean isDefaultWarehouse;

    @ApiModelProperty(value = "是否贴箱标")
    private Boolean isLabelBox;

    @ApiModelProperty(value = "装箱数量")
    private Long boxNumber;

    @ApiModelProperty(value = "增值税号")
    private String ioss;

    @ApiModelProperty(value = "COD")
    private BigDecimal codAmount;

    @ApiModelProperty(value = "可用数量，拆分SKU使用到，前端验证")
    private Integer availableInventory;

    @ApiModelProperty(value = "地址信息")
    private DelOutboundAddressVO address;

    @ApiModelProperty(value = "明细信息")
    private List<DelOutboundDetailVO> details;

    @ApiModelProperty(value = "装箱信息")
    private List<DelOutboundPackingVO> packings;

    @ApiModelProperty(value = "装箱列表,wms返回的")
    private List<DelOutboundPackingVO> containerList;

    @ApiModelProperty(value = "组合信息")
    private List<DelOutboundCombinationVO> combinations;

    @ApiModelProperty(value = "文件信息,用于一票多件")
    private List<AttachmentDataDTO> documentsFiles;

    @ApiModelProperty(value = "计费重")
    private BigDecimal calcWeight;

    @ApiModelProperty(value = "计费重单位")
    private String calcWeightUnit;

    @ApiModelProperty(value = "转运出库标签图片")
    private String shipmentRetryLabel;

    @ApiModelProperty(value = "是否上传箱标")
    private String uploadBoxLabel;


    @ApiModelProperty(value = "亚马逊物流服务id")
    private String amazonLogisticsRouteId;


    @ApiModelProperty(value = "houseNo")
    private String houseNo;
}
