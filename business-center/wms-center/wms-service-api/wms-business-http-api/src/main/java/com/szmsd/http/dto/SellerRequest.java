package com.szmsd.http.dto;

import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SellerRequest {
    @ApiModelProperty(value = "中文名")
    @Excel(name = "中文名")
    private String nameCn;

    @ApiModelProperty(value = "英文名")
    @Excel(name = "英文名")
    private String nameEn;

    @ApiModelProperty(value = "公司")
    @Excel(name = "公司")
    private String company;
    @ApiModelProperty(value = "联系电话")
    @Excel(name = "联系电话")
    private String phoneNumber;
    @ApiModelProperty(value = "用户状态 生效 失效")
    @Excel(name = "用户状态 生效 失效")
    private Boolean isActive;
    @ApiModelProperty(value = "客户代码")
    @Excel(name = "客户代码")
    private String sellerCode;

}
