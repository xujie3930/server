package com.szmsd.doc.api.delivery.request;

import com.szmsd.doc.api.delivery.request.group.DelOutboundGroup;
import com.szmsd.doc.validator.annotation.PreNotNull;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-08-03 10:10
 */
@Data
@ApiModel(value = "DelOutboundDetailRequest", description = "DelOutboundDetailRequest对象")
@PreNotNull(field = "productAttribute", model = PreNotNull.Model.VALUE, fieldValue = "Battery", linkageFields = {"electrifiedMode", "batteryPackaging"}, message = "带电信息，电池包装不能为空", groups = {DelOutboundGroup.PackageTransfer.class})
public class DelOutboundDetailRequest implements Serializable {

    @Size(max = 50, message = "SKU不能超过50个字符", groups = {DelOutboundGroup.PackageTransfer.class, DelOutboundGroup.Normal.class})
    @ApiModelProperty(value = "SKU", dataType = "String")
    private String sku;

    @NotNull(message = "数量不能为空", groups = {DelOutboundGroup.PackageTransfer.class, DelOutboundGroup.Normal.class})
    @Max(value = Integer.MAX_VALUE, message = "数量不能大于2147483647", groups = {DelOutboundGroup.PackageTransfer.class, DelOutboundGroup.Normal.class})
    @Min(value = 1, message = "数量不能小于1", groups = {DelOutboundGroup.PackageTransfer.class, DelOutboundGroup.Normal.class})
    @ApiModelProperty(value = "数量", required = true, dataType = "Long", example = "1")
    private Long qty;

    @Size(max = 50, message = "新标签不能超过50个字符", groups = {DelOutboundGroup.PackageTransfer.class, DelOutboundGroup.Normal.class})
    @ApiModelProperty(value = "新标签", dataType = "String", example = "")
    private String newLabelCode;

    @NotBlank(message = "英文申报品名不能为空", groups = {DelOutboundGroup.PackageTransfer.class})
    @Size(max = 255, message = "英文申报品名不能超过255个字符", groups = {DelOutboundGroup.PackageTransfer.class})
    @ApiModelProperty(value = "英文申报品名", required = true, dataType = "String", example = "英文申报品名")
    private String productName;

    @Size(max = 200, message = "中文申报品名不能超过200个字符", groups = {DelOutboundGroup.PackageTransfer.class})
    @ApiModelProperty(value = "中文申报品名", dataType = "String", example = "中文申报品名")
    private String productNameChinese;

    @NotNull(message = "申报价值(USD)不能为空", groups = {DelOutboundGroup.PackageTransfer.class})
    @Max(value = Integer.MAX_VALUE, message = "申报价值(USD)不能大于2147483647", groups = {DelOutboundGroup.PackageTransfer.class})
    @Min(value = 0, message = "申报价值(USD)不能小于0", groups = {DelOutboundGroup.PackageTransfer.class})
    @ApiModelProperty(value = "申报价值(USD)", dataType = "Double", example = "1.0")
    private Double declaredValue;

    @NotBlank(message = "产品属性不能为空", groups = {DelOutboundGroup.PackageTransfer.class})
    @Size(max = 50, message = "产品属性不能超过50个字符", groups = {DelOutboundGroup.PackageTransfer.class})
    @ApiModelProperty(value = "产品属性", dataType = "String")
    private String productAttribute;

    @Size(max = 50, message = "带电信息不能超过50个字符", groups = {DelOutboundGroup.PackageTransfer.class})
    @ApiModelProperty(value = "带电信息", dataType = "String")
    private String electrifiedMode;

    @Size(max = 50, message = "电池包装不能超过50个字符", groups = {DelOutboundGroup.PackageTransfer.class})
    @ApiModelProperty(value = "电池包装", dataType = "String")
    private String batteryPackaging;

    @Size(max = 50, message = "海关编码不能超过50个字符", groups = {DelOutboundGroup.PackageTransfer.class})
    @ApiModelProperty(value = "海关编码", dataType = "String")
    private String hsCode;
}
