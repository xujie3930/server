package com.szmsd.delivery.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-04-09 18:51
 */
@Data
@ApiModel(value = "DelOutboundImportDto", description = "DelOutboundImportDto对象")
public class DelOutboundImportDto implements Serializable {

    @ApiModelProperty(value = "订单顺序")
    private Integer sort;

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "出库方式")
    private String orderTypeName;

    @ApiModelProperty(value = "物流服务")
    private String shipmentRule;

    @ApiModelProperty(value = "收件人名称")
    private String consignee;

    @ApiModelProperty(value = "街道1")
    private String street1;

    @ApiModelProperty(value = "街道2")
    private String street2;

    @ApiModelProperty(value = "城镇/城市")
    private String city;

    @ApiModelProperty(value = "州/省")
    private String stateOrProvince;

    @ApiModelProperty(value = "邮编")
    private String postCode;

    @ApiModelProperty(value = "国家")
    private String country;

    @ApiModelProperty(value = "联系方式")
    private String phoneNo;

    @ApiModelProperty(value = "自提人")
    private String deliveryAgent;

    @ApiModelProperty(value = "提货人联系方式/快递单号")
    private String deliveryInfo;

    @ApiModelProperty(value = "备注")
    private String remark;
}
