package com.szmsd.doc.api.sku.request;

import com.szmsd.common.core.web.controller.QueryDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@ApiModel(description = "产品列表查询条件")
public class BaseProductQueryRequest extends QueryDto {

    @ApiModelProperty(value = "ids", hidden = true)
    private List<Long> ids;

    @ApiModelProperty(value = "英文申报品名",example = "AAAB")
    private String productName;

//    @ApiModelProperty(value = "产品中文名称")
//    private String productNameChinese;

//    @ApiModelProperty(value = "产品编码")
//    private String code;

    @ApiModelProperty(value = "SKU/编号",example = "SCNYWO7000217")
    private String codes;

    @ApiModelProperty(value = "类别",allowableValues = "SKU,包材",example = "SKU")
    private String category;

//    @NotBlank(message = "客户（卖家）编码不能为空")
    @ApiModelProperty(value = "客户（卖家）编码", required = true, hidden = true,example = "CNYWO7")
    private String sellerCode;

    @ApiModelProperty(value = "客户（卖家）编码", hidden = true)
    private String sellerCodes;

    @ApiModelProperty(value = "产品属性 [普货:GeneralCargo,电池:Battery,液体:Liquid,粉末:Powder]",allowableValues = "GeneralCargo,Battery,Liquid,Powder",example = "GeneralCargo")
    private String productAttribute;

    @ApiModelProperty(value = "是否激活", hidden = true)
    private Boolean isActive;

    @ApiModelProperty(value = "来源", hidden = true)
    private String source;
}
