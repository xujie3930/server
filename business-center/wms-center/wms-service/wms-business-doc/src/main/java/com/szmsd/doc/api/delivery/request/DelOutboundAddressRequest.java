package com.szmsd.doc.api.delivery.request;

import com.szmsd.doc.api.delivery.request.group.DelOutboundGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @author zhangyuyuan
 * @date 2021-08-03 9:56
 */
@Data
@ApiModel(value = "DelOutboundAddressRequest", description = "DelOutboundAddressRequest对象")
public class DelOutboundAddressRequest {

    @NotBlank(message = "收件人不能为空", groups = {DelOutboundGroup.PackageTransfer.class, DelOutboundGroup.Normal.class, DelOutboundGroup.Collection.class})
    @Size(max = 50, message = "收件人长度不能超过50个字符", groups = {DelOutboundGroup.Default.class})
    @ApiModelProperty(value = "收件人", required = true, dataType = "String", example = "zhangsan")
    private String consignee;

    @Size(max = 50, message = "电话号码长度不能超过50个字符", groups = {DelOutboundGroup.Default.class})
    @ApiModelProperty(value = "电话号码", dataType = "String", example = "13888888888")
    private String phoneNo;

    @Size(max = 50, message = "邮箱长度不能超过50个字符", groups = {DelOutboundGroup.Default.class})
    @ApiModelProperty(value = "邮箱", dataType = "String", example = "zhangsan@xx.com")
    private String email;

    @NotBlank(message = "街道1不能为空", groups = {DelOutboundGroup.PackageTransfer.class, DelOutboundGroup.Normal.class, DelOutboundGroup.Collection.class})
    @Size(max = 500, message = "街道1长度不能超过500个字符", groups = {DelOutboundGroup.Default.class})
    @ApiModelProperty(value = "街道1", required = true, dataType = "String", example = "street1 xxx")
    private String street1;

    @Size(max = 500, message = "街道2长度不能超过500个字符", groups = {DelOutboundGroup.Default.class})
    @ApiModelProperty(value = "街道2", dataType = "String", example = "street2 xxx")
    private String street2;

    @Size(max = 500, message = "街道3长度不能超过500个字符", groups = {DelOutboundGroup.Default.class})
    @ApiModelProperty(value = "街道3", dataType = "String", example = "street3 xxx")
    private String street3;

    @NotBlank(message = "城市不能为空", groups = {DelOutboundGroup.PackageTransfer.class, DelOutboundGroup.Normal.class, DelOutboundGroup.Collection.class, DelOutboundGroup.Batch.class})
    @Size(max = 50, message = "城市长度不能超过50个字符", groups = {DelOutboundGroup.Default.class})
    @ApiModelProperty(value = "城市", required = true, dataType = "String", example = "city xxx")
    private String city;

    @NotBlank(message = "省份/州不能为空", groups = {DelOutboundGroup.PackageTransfer.class, DelOutboundGroup.Normal.class, DelOutboundGroup.Collection.class, DelOutboundGroup.Batch.class})
    @Size(max = 50, message = "省份/州长度不能超过50个字符", groups = {DelOutboundGroup.Default.class})
    @ApiModelProperty(value = "省份/州", required = true, dataType = "String", example = "stateOrProvince xxx")
    private String stateOrProvince;

    @NotBlank(message = "国家代码不能为空", groups = {DelOutboundGroup.PackageTransfer.class, DelOutboundGroup.Normal.class, DelOutboundGroup.Collection.class})
    @Size(max = 50, message = "国家代码长度不能超过50个字符", groups = {DelOutboundGroup.Default.class})
    @ApiModelProperty(value = "国家代码", required = true, dataType = "String", example = "xxx")
    private String countryCode;

//    @NotBlank(message = "国家名称不能为空", groups = {DelOutboundGroup.PackageTransfer.class, DelOutboundGroup.Normal.class, DelOutboundGroup.Collection.class})
//    @Size(max = 50, message = "国家名称长度不能超过50个字符", groups = {DelOutboundGroup.Default.class})
//    @ApiModelProperty(value = "国家名称", required = true, dataType = "String", example = "xxx")
//    private String country;

    @NotBlank(message = "邮编不能为空", groups = {DelOutboundGroup.PackageTransfer.class, DelOutboundGroup.Normal.class, DelOutboundGroup.Collection.class})
    @Size(max = 50, message = "邮编长度不能超过50个字符", groups = {DelOutboundGroup.Default.class})
    @ApiModelProperty(value = "邮编", required = true, dataType = "String", example = "123456")
    private String postCode;

    @Size(max = 50, message = "区域长度不能超过50个字符", groups = {DelOutboundGroup.Default.class})
    @ApiModelProperty(value = "区域", dataType = "String", example = "xxx")
    private String zone;

}
