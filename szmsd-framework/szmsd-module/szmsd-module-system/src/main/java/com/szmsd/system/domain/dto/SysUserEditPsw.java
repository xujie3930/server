package com.szmsd.system.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "用户修改密码实体")
public class SysUserEditPsw {

    @ApiModelProperty(value = "老密码")
    private String oldPassword;

    @ApiModelProperty(value = "新密码")
    private String newPassword;

    @ApiModelProperty(value = "用户类型：00-系统用户 01-VIP用户")
    private String userType;

    @ApiModelProperty(value = "类型：1-PC，2-APP，3-VIP大客户")
    private Integer type;

}
