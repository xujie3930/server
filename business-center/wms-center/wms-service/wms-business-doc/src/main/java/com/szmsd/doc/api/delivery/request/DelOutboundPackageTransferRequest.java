package com.szmsd.doc.api.delivery.request;

import com.szmsd.doc.api.SwaggerDictionary;
import com.szmsd.doc.api.delivery.request.group.DelOutboundGroup;
import com.szmsd.doc.validator.DictionaryPluginConstant;
import com.szmsd.doc.validator.annotation.Dictionary;
import com.szmsd.doc.validator.annotation.PreNotNull;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 转运出库
 *
 * @author zhangyuyuan
 * @date 2021-08-03 9:46
 */
@Data
@ApiModel(value = "DelOutboundPackageTransferRequest", description = "DelOutboundPackageTransferRequest对象")
@PreNotNull(field = "packageConfirm", fieldValue = "076002", model = PreNotNull.Model.VALUE, linkageFields = {"packageWeightDeviation"}, message = "重量误差范围不能为空")
public class DelOutboundPackageTransferRequest implements Serializable {

    /*@NotBlank(message = "客户编码不能为空", groups = {DelOutboundGroup.PackageTransfer.class})
    @ApiModelProperty(value = "客户编码", required = true, dataType = "String")
    private String sellerCode;*/

    @Dictionary(message = "仓库编码不存在", type = DictionaryPluginConstant.WAR_DICTIONARY_PLUGIN, groups = {DelOutboundGroup.Default.class})
    @NotBlank(message = "仓库编码不能为空", groups = {DelOutboundGroup.PackageTransfer.class})
    @ApiModelProperty(value = "仓库编码", required = true, dataType = "String")
    private String warehouseCode;

    @NotNull(message = "重量不能为空", groups = {DelOutboundGroup.PackageTransfer.class})
    @Max(value = Integer.MAX_VALUE, message = "重量不能大于2147483647", groups = {DelOutboundGroup.PackageTransfer.class})
    @Min(value = 0, message = "重量不能小于0", groups = {DelOutboundGroup.PackageTransfer.class})
    @ApiModelProperty(value = "重量 g", required = true, dataType = "Double", position = 1, example = "10")
    private Double weight;

    @NotNull(message = "长不能为空", groups = {DelOutboundGroup.PackageTransfer.class})
    @Max(value = Integer.MAX_VALUE, message = "长不能大于2147483647", groups = {DelOutboundGroup.PackageTransfer.class})
    @Min(value = 0, message = "长不能小于0", groups = {DelOutboundGroup.PackageTransfer.class})
    @ApiModelProperty(value = "长 CM", required = true, dataType = "Double", position = 2, example = "10")
    private Double length;

    @NotNull(message = "宽不能为空", groups = {DelOutboundGroup.PackageTransfer.class})
    @Max(value = Integer.MAX_VALUE, message = "宽不能大于2147483647", groups = {DelOutboundGroup.PackageTransfer.class})
    @Min(value = 0, message = "宽不能小于0", groups = {DelOutboundGroup.PackageTransfer.class})
    @ApiModelProperty(value = "宽 CM", required = true, dataType = "Double", position = 3, example = "10")
    private Double width;

    @NotNull(message = "高不能为空", groups = {DelOutboundGroup.PackageTransfer.class})
    @Max(value = Integer.MAX_VALUE, message = "高不能大于2147483647", groups = {DelOutboundGroup.PackageTransfer.class})
    @Min(value = 0, message = "高不能小于0", groups = {DelOutboundGroup.PackageTransfer.class})
    @ApiModelProperty(value = "高 CM", required = true, dataType = "Double", position = 4, example = "10")
    private Double height;

    @Dictionary(message = "重量尺寸确认不存在", type = DictionaryPluginConstant.SUB_DICTIONARY_PLUGIN, param = "076", groups = {DelOutboundGroup.Default.class})
    @SwaggerDictionary(dicCode = "076")
    @NotBlank(message = "重量尺寸确认不能为空", groups = {DelOutboundGroup.PackageTransfer.class})
    @Size(max = 30, message = "重量尺寸确认不能超过30个字符", groups = {DelOutboundGroup.PackageTransfer.class})
    @ApiModelProperty(value = "重量尺寸确认", required = true, dataType = "String", position = 5, example = "076001")
    private String packageConfirm;

    @Max(value = Integer.MAX_VALUE, message = "重量误差范围不能大于2147483647", groups = {DelOutboundGroup.PackageTransfer.class})
    @Min(value = 0, message = "重量误差范围不能小于0", groups = {DelOutboundGroup.PackageTransfer.class})
    @ApiModelProperty(value = "重量误差范围 g", dataType = "Integer", position = 6, example = "50")
    private Integer packageWeightDeviation;

    @Size(max = 50, message = "增值税号不能超过50个字符", groups = {DelOutboundGroup.PackageTransfer.class})
    @ApiModelProperty(value = "增值税号", dataType = "String", position = 7, example = "F00X")
    private String ioss;

    @Min(value = 0, message = "COD不能小于0", groups = {DelOutboundGroup.Default.class})
    @ApiModelProperty(value = "COD", dataType = "Double", position = 8, example = "0.0")
    private BigDecimal codAmount;

    @NotBlank(message = "物流服务不能为空", groups = {DelOutboundGroup.PackageTransfer.class})
    @Size(max = 50, message = "物流服务不能超过50个字符", groups = {DelOutboundGroup.PackageTransfer.class})
    @ApiModelProperty(value = "物流服务", dataType = "String", position = 9, example = "FX")
    private String shipmentRule;

//    @Pattern(message = "参考号只能是数字或英文字母", regexp = "^[A-Za-z0-9]*$", groups = {DelOutboundGroup.Default.class})
    @Size(max = 50, message = "参考号不能超过50个字符", groups = {DelOutboundGroup.PackageTransfer.class})
    @ApiModelProperty(value = "参考号", dataType = "String", position = 10, example = "")
    private String refNo;

    @Size(max = 500, message = "备注不能超过500个字符", groups = {DelOutboundGroup.PackageTransfer.class})
    @ApiModelProperty(value = "备注", dataType = "String", position = 11, example = "")
    private String remark;

    @ApiModelProperty(value = "伙伴编码")
    private String partnerCode;

    @Valid
    @NotNull(message = "地址信息不能为空", groups = {DelOutboundGroup.Default.class})
    @ApiModelProperty(value = "地址信息", dataType = "DelOutboundAddressRequest", position = 12)
    private DelOutboundAddressRequest address;

    @Valid
    @NotNull(message = "明细信息不能为空", groups = {DelOutboundGroup.Default.class})
    @ApiModelProperty(value = "明细信息", dataType = "DelOutboundPackageTransferDetailRequest", position = 13)
    private List<DelOutboundPackageTransferDetailRequest> details;
}
