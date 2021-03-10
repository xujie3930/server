package com.szmsd.bas.dto;

import com.szmsd.bas.domain.BasSeller;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BasSellerDto extends BasSeller {
    @ApiModelProperty("验证码")
    String captcha;
    @ApiModelProperty(value = "业务经理姓名")
    @Excel(name = "业务经理姓名")
    private String serviceManagerName;

    @ApiModelProperty(value = "确认密码")
    @Excel(name = "确认密码")
    private String confirmPassword;

    @ApiModelProperty(value = "密码")
    @Excel(name = "密码")
    private String password;

}
