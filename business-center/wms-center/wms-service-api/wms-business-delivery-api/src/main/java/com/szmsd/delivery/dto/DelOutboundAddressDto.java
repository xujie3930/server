package com.szmsd.delivery.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-03-05 14:23
 */
@Data
@ApiModel(value = "DelOutboundAddressDto", description = "DelOutboundAddressDto对象")
public class DelOutboundAddressDto implements Serializable {

    @ApiModelProperty(value = "ID")
    private Long id;

    @NotBlank(message = "收件人不能为空")
    @ApiModelProperty(value = "收件人")
    private String consignee;

    @ApiModelProperty(value = "国家代码")
    private String countryCode;

    @ApiModelProperty(value = "国家名称")
    private String country;

    @ApiModelProperty(value = "区域")
    private String zone;

    @ApiModelProperty(value = "省份/洲")
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
