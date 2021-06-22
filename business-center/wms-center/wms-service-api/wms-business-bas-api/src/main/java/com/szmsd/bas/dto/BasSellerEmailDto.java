package com.szmsd.bas.dto;

import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @FileName BasSellerEmailDto
 * @Description ---------- ---------
 * @Date 2021-06-21
 * @Author jr
 * @Version 1.0
 */
@Data
public class BasSellerEmailDto {
    @ApiModelProperty(value = "初始注册邮箱")
    @Excel(name = "初始注册邮箱")
    private String initEmail;
    @ApiModelProperty(value = "客户代码")
    @Excel(name = "客户代码")
    private String sellerCode;
}
