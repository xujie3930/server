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
@ApiModel(value = "DelOutboundImportDto", description = "DelOutboundImportDto对象")
public class DelOutboundEnImportDto implements Serializable {

    @ApiModelProperty(value = "Order Row")
    private Integer sort;

    @ApiModelProperty(value = "Warehouse Code")
    private String warehouseCode;

    @ApiModelProperty(value = "Outbound method")
    private String orderTypeName;

    @ApiModelProperty(value = "Logistics service")
    private String shipmentRule;

    @ApiModelProperty(value = "Addressee's name")
    private String consignee;

    @ApiModelProperty(value = "Address1")
    private String street1;

    @ApiModelProperty(value = "Address2")
    private String street2;

    @ApiModelProperty(value = "City")
    private String city;

    @ApiModelProperty(value = "State/province")
    private String stateOrProvince;

    @ApiModelProperty(value = "Postcode")
    private String postCode;

    @ApiModelProperty(value = "Country")
    private String country;

    @ApiModelProperty(value = "Contact Information")
    private String phoneNo;

    @ApiModelProperty(value = "E-mail")
    private String email;

    @ApiModelProperty(value = "Pickup Method")
    private String deliveryMethodName;

    @ApiModelProperty(value = "Estimated time of delivery")
    private Date deliveryTime;

    @ApiModelProperty(value = "Consignee")
    private String deliveryAgent;

    @ApiModelProperty(value = "Consignee's Number\n" +
            "/Tracking Number")
    private String deliveryInfo;

    @ApiModelProperty(value = "Remarks")
    private String remark;

    @ApiModelProperty(value = "COD")
    private BigDecimal codAmount;


}
