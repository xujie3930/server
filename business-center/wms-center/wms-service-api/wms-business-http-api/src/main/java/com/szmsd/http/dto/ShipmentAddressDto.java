package com.szmsd.http.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-03-09 10:52
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "ShipmentAddressDto", description = "ShipmentAddressDto对象")
public class ShipmentAddressDto implements Serializable {

    @ApiModelProperty(value = "收件人")
    private String consignee;

    @ApiModelProperty(value = "国家代码")
    private String countryCode;

    @ApiModelProperty(value = "国家名称")
    private String country;

    @ApiModelProperty(value = "区域")
    private String zone;

    @ApiModelProperty(value = "省份/州")
    private String stateOrProvince;

    @ApiModelProperty(value = "城市")
    private String city;

    @ApiModelProperty(value = "街道1")
    private String street1;

    @ApiModelProperty(value = "街道2")
    private String street2;

    @ApiModelProperty(value = "街道3")
    private String street3;

    @ApiModelProperty(value = "邮编")
    private String postCode;

    @ApiModelProperty(value = "电话号码")
    private String phoneNo;

    @ApiModelProperty(value = "邮箱")
    private String email;
}
