package com.szmsd.doc.api.delivery.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @author zhangyuyuan
 * @date 2021-08-03 9:56
 */
@Data
@ApiModel(value = "DelOutboundAddressRequest", description = "DelOutboundAddressRequest对象")
public class DelOutboundAddressRequest {

    @NotBlank(message = "收件人不能为空")
    @Length(max = 50, message = "收件人长度不能超过50个字符")
    @ApiModelProperty(value = "收件人", required = true, dataType = "String", example = "zhangsan")
    private String consignee;

    @Length(max = 50, message = "电话号码长度不能超过50个字符")
    @ApiModelProperty(value = "电话号码", dataType = "String", example = "13888888888")
    private String phoneNo;

    @Length(max = 50, message = "邮箱长度不能超过50个字符")
    @ApiModelProperty(value = "邮箱", dataType = "String", example = "zhangsan@xx.com")
    private String email;

    @NotBlank(message = "街道1不能为空")
    @Length(max = 500, message = "街道1长度不能超过500个字符")
    @ApiModelProperty(value = "街道1", required = true, dataType = "String", example = "street1 xxx")
    private String street1;

    @Length(max = 500, message = "街道2长度不能超过500个字符")
    @ApiModelProperty(value = "街道2", dataType = "String", example = "street2 xxx")
    private String street2;

    @Length(max = 500, message = "街道3长度不能超过500个字符")
    @ApiModelProperty(value = "街道3", dataType = "String", example = "street3 xxx")
    private String street3;

    @Length(max = 50, message = "城市长度不能超过50个字符")
    @ApiModelProperty(value = "城市", dataType = "String", example = "city xxx")
    private String city;

    @Length(max = 50, message = "省份/洲长度不能超过50个字符")
    @ApiModelProperty(value = "省份/洲", dataType = "String", example = "stateOrProvince xxx")
    private String stateOrProvince;

    @NotBlank(message = "国家代码不能为空")
    @Length(max = 50, message = "国家代码长度不能超过50个字符")
    @ApiModelProperty(value = "国家代码", required = true, dataType = "String", example = "xxx")
    private String countryCode;

    @NotBlank(message = "国家名称不能为空")
    @Length(max = 50, message = "国家名称长度不能超过50个字符")
    @ApiModelProperty(value = "国家名称", required = true, dataType = "String", example = "xxx")
    private String country;

    @NotBlank(message = "邮编不能为空")
    @Length(max = 50, message = "邮编长度不能超过50个字符")
    @ApiModelProperty(value = "邮编", required = true, dataType = "String", example = "123456")
    private String postCode;

    @Length(max = 50, message = "区域长度不能超过50个字符")
    @ApiModelProperty(value = "区域", dataType = "String", example = "xxx")
    private String zone;

}
