package com.szmsd.system.api.domain.dto;

import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SysUserByTypeAndUserType {
    @ApiModelProperty(value = "用户名")
    String username;

    @Excel(name = "用户名称")
    @ApiModelProperty(value = "用户名称")
    private String nickName;

    @ApiModelProperty(value = "类型：1-PC，2-APP，3-VIP")
    Integer type;

    @ApiModelProperty(value = "用户类型（00-系统用户 01-VIP用户")
    String userType;
}
