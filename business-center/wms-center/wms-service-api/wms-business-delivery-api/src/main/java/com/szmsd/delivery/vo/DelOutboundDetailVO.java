package com.szmsd.delivery.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author zhangyuyuan
 * @date 2021-03-05 14:23
 */
@Data
@ApiModel(value = "DelOutboundDetailVO", description = "DelOutboundDetailVO对象")
public class DelOutboundDetailVO implements Serializable {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "SKU")
    private String sku;

    @ApiModelProperty(value = "数量")
    private Long qty;

    @ApiModelProperty(value = "指定编码")
    private String newLabelCode;

    @ApiModelProperty(value = "可用库存")
    private Integer availableInventory;

    @ApiModelProperty(value = "产品名称")
    private String productName;

    @ApiModelProperty(value = "初始重量g")
    private Double initWeight;

    @ApiModelProperty(value = "初始长 cm")
    private Double initLength;

    @ApiModelProperty(value = "初始宽 cm")
    private Double initWidth;

    @ApiModelProperty(value = "初始高 cm")
    private Double initHeight;

    @ApiModelProperty(value = "初始体积 cm3")
    private BigDecimal initVolume;

    @ApiModelProperty(value = "仓库测量重量g")
    private Double weight;

    @ApiModelProperty(value = "仓库测量长 cm")
    private Double length;

    @ApiModelProperty(value = "仓库测量宽 cm")
    private Double width;

    @ApiModelProperty(value = "仓库测量高 cm")
    private Double height;

    @ApiModelProperty(value = "仓库测量体积 cm3")
    private BigDecimal volume;

    @ApiModelProperty(value = "绑定专属包材产品编码")
    private String bindCode;

    @ApiModelProperty(value = "绑定专属包材产品名")
    private String bindCodeName;
}
