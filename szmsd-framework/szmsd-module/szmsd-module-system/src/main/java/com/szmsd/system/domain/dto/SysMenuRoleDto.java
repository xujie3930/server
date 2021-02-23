package com.szmsd.system.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "角色菜单类型实体DTO", description = "sys_menu")
public class SysMenuRoleDto {

    @ApiModelProperty(value = "角色id")
   private Long roleId;

    @ApiModelProperty(value = "权限类型：1-PC，2-APP")
    private  Integer type;
}
