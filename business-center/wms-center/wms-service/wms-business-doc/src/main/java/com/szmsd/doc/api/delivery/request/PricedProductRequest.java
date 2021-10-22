package com.szmsd.doc.api.delivery.request;

import com.szmsd.doc.api.SwaggerDictionary;
import com.szmsd.doc.validator.DictionaryPluginConstant;
import com.szmsd.doc.validator.annotation.Dictionary;
import com.szmsd.doc.validator.annotation.NotAnyNull;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-07-29 16:29
 */
@Data
@ApiModel(value = "PricedProductRequest", description = "PricedProductRequest对象")
@NotAnyNull(fields = {"skus", "productAttributes"}, message = "SKU，产品属性信息必须其中一个有值")
public class PricedProductRequest implements Serializable {

    /*@NotBlank(message = "客户代码不能为空")
    @ApiModelProperty(value = "客户代码", required = true, dataType = "String", position = 1, example = "CS045")
    private String clientCode;*/

    @Dictionary(message = "仓库编码不存在", type = DictionaryPluginConstant.WAR_DICTIONARY_PLUGIN)
    // @NotBlank(message = "仓库编码不能为空")
    @ApiModelProperty(value = "仓库编码，需要去#仓库信息接口中查询", required = false, dataType = "String", position = 2, example = "GZ")
    private String warehouseCode;

    // @NotBlank(message = "国家编码不能为空")
    @ApiModelProperty(value = "国家编码", required = false, dataType = "String", position = 3, example = "CN")
    private String countryCode;

    @ApiModelProperty(value = "SKU，产品属性信息二选一", dataType = "String", position = 4, example = "[\"SN001\", \"SN002\"]")
    private List<String> skus;

    /**
     * "<br/>普货：GeneralCargo" +
     * "<br/>电池：Battery" +
     * "<br/>液体：Liquid" +
     * "<br/>粉末：Powder" +
     * "<br/>磁铁：Magnet"
     */
    @Dictionary(message = "产品属性不存在", type = DictionaryPluginConstant.SUB_DICTIONARY_PLUGIN, param = "&&059")
    @SwaggerDictionary(dicCode = "059", dicKey = "subValue")
    @ApiModelProperty(value = "产品属性信息，SKU二选一", dataType = "String", position = 4, example = "[\"GeneralCargo\", \"Powder\"]")
    private List<String> productAttributes;

}
