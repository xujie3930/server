package com.szmsd.doc.api.delivery.request;

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

    @NotBlank(message = "客户代码不能为空")
    @ApiModelProperty(value = "客户代码", required = true, dataType = "String", position = 1, example = "CS045")
    private String clientCode;

    @NotBlank(message = "仓库编码不能为空")
    @ApiModelProperty(value = "仓库编码", required = true, dataType = "String", position = 2, example = "GZ")
    private String warehouseCode;

    @NotBlank(message = "国家编码不能为空")
    @ApiModelProperty(value = "国家编码", required = true, dataType = "String", position = 3, example = "CN")
    private String countryCode;

    @ApiModelProperty(value = "SKU，产品属性信息二选一", dataType = "String", position = 4, example = "[\"SN001\", \"SN002\"]")
    private List<String> skus;

    @ApiModelProperty(value = "产品属性信息，SKU二选一" +
            "<br/>普货：GeneralCargo" +
            "<br/>电池：Battery" +
            "<br/>液体：Liquid" +
            "<br/>粉末：Powder" +
            "<br/>磁铁：Magnet", dataType = "String", position = 4, example = "[\"GeneralCargo\", \"Powder\"]")
    private List<String> productAttributes;

    private String client;
}
