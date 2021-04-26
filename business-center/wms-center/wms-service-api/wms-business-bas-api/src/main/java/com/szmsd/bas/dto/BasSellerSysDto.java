package com.szmsd.bas.dto;

import com.szmsd.bas.domain.BasSeller;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BasSellerSysDto extends BasSeller {
    private Long sysId;
    @ApiModelProperty(value = "审核状态")
    private Boolean reviewState;
}
