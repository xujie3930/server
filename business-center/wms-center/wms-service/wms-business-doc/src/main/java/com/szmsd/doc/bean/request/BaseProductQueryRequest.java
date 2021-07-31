package com.szmsd.doc.bean.request;

import com.szmsd.common.core.web.controller.QueryDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class BaseProductQueryRequest extends QueryDto {

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

    @ApiModelProperty(value = "客户（卖家）编码")
    private String sellerCode;

    @ApiModelProperty(value = "客户（卖家）编码")
    private String sellerCodes;

    @ApiModelProperty(value = "产品属性编号")
    private String productAttribute;

    @ApiModelProperty(value = "是否激活")
    private Boolean isActive;

    @ApiModelProperty(value = "来源")
    private String source;
}
