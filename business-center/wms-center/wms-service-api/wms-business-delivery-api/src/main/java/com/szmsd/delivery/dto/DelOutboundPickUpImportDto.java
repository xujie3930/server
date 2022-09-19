package com.szmsd.delivery.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zhangyuyuan
 * @date 2021-04-09 18:51
 */
@Data
@ApiModel(value = "DelOutboundPickUpImportDto", description = "DelOutboundPickUpImportDto对象")
public class DelOutboundPickUpImportDto implements Serializable {

    @ApiModelProperty(value = "订单顺序")
    private Integer sort;

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "出库方式")
    private String orderTypeName;

    @ApiModelProperty(value = "提货方式")
    private String deliveryMethodName;

    @ApiModelProperty(value = "提货时间")
    private Date deliveryTime;

    @ApiModelProperty(value = "提货商/快递商")
    private String deliveryAgent;

    @ApiModelProperty(value = "提货人联系方式/快递单号")
    private String deliveryInfo;


    @ApiModelProperty(value = "RefNo")
    private String refNo;

    @ApiModelProperty(value = "增值税号\n" +
            "（非必填）")
    private String ioss;


    @ApiModelProperty(value = "COD")
    private BigDecimal codAmount;

    @ApiModelProperty(value = "备注")
    private String remark;



}
