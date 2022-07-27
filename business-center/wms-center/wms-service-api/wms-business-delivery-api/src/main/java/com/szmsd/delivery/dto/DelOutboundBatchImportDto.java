package com.szmsd.delivery.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangyuyuan
 */
@Data
@ApiModel(value = "DelOutboundBatchImportDto", description = "DelOutboundBatchImportDto对象")
public class DelOutboundBatchImportDto implements Serializable {

    @ApiModelProperty(value = "订单顺序")
    private Integer sort;

    @ApiModelProperty(value = "出库方式")
    private String orderType;

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "是否贴箱标")
    private String labelBox;

    @ApiModelProperty(value = "是否必须按要求装箱")
    private String packingByRequired;

    @ApiModelProperty(value = "装箱数量")
    private Long boxNumber;

    @ApiModelProperty(value = "是否默认仓库装箱数据")
    private String defaultWarehouse;

    @ApiModelProperty(value = "出货渠道")
    private String shipmentChannel;

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

    @ApiModelProperty(value = "提货商/快递商")
    private String deliveryAgent;

    @ApiModelProperty(value = "提货/快递信息")
    private String deliveryInfo;

    @ApiModelProperty(value = "备注")
    private String remark;


    @ApiModelProperty(value = "电子邮箱")
    private String email;

}
