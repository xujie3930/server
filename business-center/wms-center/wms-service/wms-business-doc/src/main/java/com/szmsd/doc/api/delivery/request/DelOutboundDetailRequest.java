package com.szmsd.doc.api.delivery.request;

import com.szmsd.doc.api.delivery.request.group.DelOutboundGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-08-03 10:10
 */
@Data
@ApiModel(value = "DelOutboundDetailRequest", description = "DelOutboundDetailRequest对象")
public class DelOutboundDetailRequest implements Serializable {

    @ApiModelProperty(value = "SKU")
    private String sku;

    @NotNull(message = "数量不能为空", groups = {DelOutboundGroup.PackageTransfer.class})
    @Size(message = "数量必须介于0~2147483647之间", groups = {DelOutboundGroup.PackageTransfer.class})
    @ApiModelProperty(value = "数量")
    private Long qty;

    @ApiModelProperty(value = "指定编码")
    private String newLabelCode;

    @NotBlank(message = "英文申报品名", groups = {DelOutboundGroup.PackageTransfer.class})
    @Length(max = 255, message = "英文申报品名不能超过255个字符", groups = {DelOutboundGroup.PackageTransfer.class})
    @ApiModelProperty(value = "英文申报品名")
    private String productName;

    @Length(max = 200, message = "中文申报品名不能超过200个字符", groups = {DelOutboundGroup.PackageTransfer.class})
    @ApiModelProperty(value = "中文申报品名")
    private String productNameChinese;

    @NotNull(message = "申报价值(USD)不能为空", groups = {DelOutboundGroup.PackageTransfer.class})
    @Size(message = "申报价值(USD)必须介于0~2147483647之间", groups = {DelOutboundGroup.PackageTransfer.class})
    @ApiModelProperty(value = "申报价值(USD)")
    private Double declaredValue;

    @NotBlank(message = "产品属性不能为空", groups = {DelOutboundGroup.PackageTransfer.class})
    @Length(max = 50, message = "产品属性不能超过50个字符", groups = {DelOutboundGroup.PackageTransfer.class})
    @ApiModelProperty(value = "产品属性")
    private String productAttribute;

    @Length(max = 50, message = "带电信息不能超过50个字符", groups = {DelOutboundGroup.PackageTransfer.class})
    @ApiModelProperty(value = "带电信息")
    private String electrifiedMode;

    @Length(max = 50, message = "电池包装不能超过50个字符", groups = {DelOutboundGroup.PackageTransfer.class})
    @ApiModelProperty(value = "电池包装")
    private String batteryPackaging;

    @Length(max = 50, message = "海关编码不能超过50个字符", groups = {DelOutboundGroup.PackageTransfer.class})
    @ApiModelProperty(value = "海关编码")
    private String hsCode;
}
