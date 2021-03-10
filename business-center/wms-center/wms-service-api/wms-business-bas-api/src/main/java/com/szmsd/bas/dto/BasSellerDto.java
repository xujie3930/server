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
}
