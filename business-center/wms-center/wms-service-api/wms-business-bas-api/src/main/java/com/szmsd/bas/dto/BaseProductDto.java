package com.szmsd.bas.dto;

import com.szmsd.bas.domain.BaseProduct;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BaseProductDto extends BaseProduct {
    @ApiModelProperty(value = "产品图片Base64")
    @Excel(name = "产品图片Base64")
    private String productImageBase64;
}
