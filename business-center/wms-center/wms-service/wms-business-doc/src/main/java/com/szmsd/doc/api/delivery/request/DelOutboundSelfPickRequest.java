package com.szmsd.doc.api.delivery.request;

import com.szmsd.doc.api.SwaggerDictionary;
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
import java.util.Date;
import java.util.List;

@Data
@ApiModel(value = "DelOutboundSelfPickRequest", description = "DelOutboundSelfPickRequest对象")
public class DelOutboundSelfPickRequest implements Serializable {

    /*@NotBlank(message = "客户编码不能为空", groups = {DelOutboundGroup.SelfPick.class})
    @ApiModelProperty(value = "客户编码", required = true, dataType = "String")
    private String sellerCode;*/

    @Dictionary(message = "仓库编码不存在", type = DictionaryPluginConstant.WAR_DICTIONARY_PLUGIN, groups = {DelOutboundGroup.Default.class})
    @NotBlank(message = "仓库编码不能为空", groups = {DelOutboundGroup.SelfPick.class})
    @ApiModelProperty(value = "仓库编码", required = true, dataType = "String")
    private String warehouseCode;

    @ApiModelProperty(value = "是否优先发货", dataType = "Boolean", position = 1, example = "false")
    private Boolean isFirst;

    @Dictionary(message = "提货方式不存在", type = DictionaryPluginConstant.SUB_DICTIONARY_PLUGIN, param = "&&058", groups = {DelOutboundGroup.Default.class})
    @SwaggerDictionary(dicCode = "058", dicKey = "subValue")
    @NotBlank(message = "提货方式不能为空", groups = {DelOutboundGroup.SelfPick.class})
    @ApiModelProperty(value = "提货方式", dataType = "String", required = true, position = 2, example = "")
    private String deliveryMethod;

    @NotNull(message = "提货时间不能为空", groups = {DelOutboundGroup.SelfPick.class})
    @ApiModelProperty(value = "提货时间", required = true,dataType = "Date", position = 3, example = "")
    private Date deliveryTime;

    @Size(max = 200, message = "提货商/快递商不能超过200个字符", groups = {DelOutboundGroup.SelfPick.class})
    @NotBlank(message = "提货商/快递商不能为空", groups = {DelOutboundGroup.SelfPick.class})
    @ApiModelProperty(value = "提货商/快递商", dataType = "String", required = true, position = 4, example = "")
    private String deliveryAgent;

    //@NotBlank(message = "提货/快递信息不能为空", groups = {DelOutboundGroup.SelfPick.class})
    @Size(max = 200, message = "提货/快递信息不能超过200个字符")
    @ApiModelProperty(value = "提货/快递信息", dataType = "String", position = 5, example = "")
    private String deliveryInfo;

    @Pattern(message = "参考号只能是数字", regexp = "^[0-9]*$", groups = {DelOutboundGroup.Default.class})
    @Size(max = 50, message = "参考号不能超过50个字符", groups = {DelOutboundGroup.SelfPick.class})
    @ApiModelProperty(value = "参考号", dataType = "String", position = 6, example = "")
    private String refNo;

    @Size(max = 50, message = "增值税号不能超过50个字符", groups = {DelOutboundGroup.SelfPick.class})
    @ApiModelProperty(value = "增值税号", dataType = "String", position = 7, example = "F00X")
    private String ioss;

    @Min(value = 0, message = "COD不能小于0", groups = {DelOutboundGroup.Default.class})
    @ApiModelProperty(value = "COD", dataType = "Double", position = 8, example = "0.0")
    private BigDecimal codAmount;

    @Size(max = 500, message = "备注不能超过500个字符", groups = {DelOutboundGroup.SelfPick.class})
    @ApiModelProperty(value = "备注", dataType = "String", position = 9, example = "")
    private String remark;

    @Valid
    @NotNull(message = "明细信息不能为空", groups = {DelOutboundGroup.Default.class})
    @ApiModelProperty(value = "明细信息", dataType = "DelOutboundSkuDetailNoLabelRequest", position = 10)
    private List<DelOutboundSkuDetailNoLabelRequest> details;

}
