package com.szmsd.doc.api.sku.request;

import com.szmsd.common.core.web.controller.QueryDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class BaseProductQueryRequest extends QueryDto {

    @ApiModelProperty(value = "ids", hidden = true)
    private List<Long> ids;

    @ApiModelProperty(value = "产品名称")
    private String productName;

    @ApiModelProperty(value = "产品中文名称")
    private String productNameChinese;

    @ApiModelProperty(value = "产品编码")
    private String code;

    @ApiModelProperty(value = "产品编码")
    private String codes;

    @ApiModelProperty(value = "sku/包材")
    private String category;
    @NotBlank(message = "客户（卖家）编码不能为空")
    @ApiModelProperty(value = "客户（卖家）编码", required = true)
    private String sellerCode;

    @ApiModelProperty(value = "客户（卖家）编码", hidden = true)
    private String sellerCodes;

    @ApiModelProperty(value = "产品属性编号")
    private String productAttribute;

    @ApiModelProperty(value = "是否激活", hidden = true)
    private Boolean isActive;

    @ApiModelProperty(value = "来源")
    private String source;
}
