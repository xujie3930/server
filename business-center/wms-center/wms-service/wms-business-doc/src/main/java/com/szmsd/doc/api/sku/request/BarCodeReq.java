package com.szmsd.doc.api.sku.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @ClassName: BarCodeReq
 * @Description: sku标签生成
 * @Author: 11
 * @Date: 2021-09-16 10:24
 */
@Data
@ApiModel(description = "产品列表查询条件")
public class BarCodeReq {
    @Size(max = 200, message = "SKU/编号仅支持200字符长度")
    @NotBlank(message = "SKU/编号不能为空")
    @ApiModelProperty(value = "SKU/编号", example = "SCNYWO7000217")
    private String skuCode;
}
