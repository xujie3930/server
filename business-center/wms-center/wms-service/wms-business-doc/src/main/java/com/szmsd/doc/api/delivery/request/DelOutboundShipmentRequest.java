package com.szmsd.doc.api.delivery.request;

import com.szmsd.doc.api.delivery.request.group.DelOutboundGroup;
import com.szmsd.doc.validator.DictionaryPluginConstant;
import com.szmsd.doc.validator.annotation.Dictionary;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 一件代发
 *
 * @author zhangyuyuan
 * @date 2021-08-03 9:46
 */
@Data
@ApiModel(value = "DelOutboundShipmentRequest", description = "DelOutboundShipmentRequest对象")
public class DelOutboundShipmentRequest implements Serializable {

    /*@NotBlank(message = "客户编码不能为空", groups = {DelOutboundGroup.Default.class})
    @ApiModelProperty(value = "客户编码", required = true, dataType = "String")
    private String sellerCode;*/

    @Dictionary(message = "仓库编码不存在", type = DictionaryPluginConstant.WAR_DICTIONARY_PLUGIN, groups = {DelOutboundGroup.Default.class})
    @NotBlank(message = "仓库编码不能为空", groups = {DelOutboundGroup.Default.class})
    @ApiModelProperty(value = "仓库编码", required = true, dataType = "String")
    private String warehouseCode;

    @ApiModelProperty(value = "是否优先发货", dataType = "Boolean", position = 1, example = "false")
    private Boolean isFirst;

    @Size(max = 50, message = "增值税号不能超过50个字符", groups = {DelOutboundGroup.Normal.class})
    @ApiModelProperty(value = "增值税号", dataType = "String", position = 2, example = "F00X")
    private String ioss;

    @Min(value = 0, message = "COD不能小于0", groups = {DelOutboundGroup.Default.class})
    @ApiModelProperty(value = "COD", dataType = "Double", position = 3, example = "0.0")
    private BigDecimal codAmount;

    @NotBlank(message = "物流服务不能为空", groups = {DelOutboundGroup.Normal.class})
    @Size(max = 50, message = "物流服务不能超过50个字符", groups = {DelOutboundGroup.Normal.class})
    @ApiModelProperty(value = "物流服务", dataType = "String", position = 4, example = "FX")
    private String shipmentRule;

//    @Pattern(message = "参考号只能是数字", regexp = "^[0-9]*$", groups = {DelOutboundGroup.Default.class})
    @Size(max = 50, message = "参考号不能超过50个字符", groups = {DelOutboundGroup.Normal.class})
    @ApiModelProperty(value = "参考号", dataType = "String", position = 5, example = "")
    private String refNo;

    @Size(max = 500, message = "备注不能超过500个字符", groups = {DelOutboundGroup.Normal.class})
    @ApiModelProperty(value = "备注", dataType = "String", position = 6, example = "")
    private String remark;

    @ApiModelProperty(value = "伙伴编码")
    private String partnerCode;


    @ApiModelProperty(value = "houseNo")
    private String houseNo;

    @Valid
    @NotNull(message = "地址信息不能为空", groups = {DelOutboundGroup.Normal.class})
    @ApiModelProperty(value = "地址信息", dataType = "DelOutboundAddressRequest", position = 7)
    private DelOutboundAddressRequest address;

    @Valid
    @NotNull(message = "明细信息不能为空", groups = {DelOutboundGroup.Default.class})
    @ApiModelProperty(value = "明细信息", dataType = "DelOutboundSkuDetailNoLabelRequest", position = 8)
    private List<DelOutboundSkuDetailNoLabelRequest> details;

}
