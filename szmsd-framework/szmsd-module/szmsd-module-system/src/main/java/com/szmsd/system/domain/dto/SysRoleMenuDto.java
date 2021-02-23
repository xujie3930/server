package com.szmsd.system.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(value="角色和菜单关联Dto", description="SysRoleMenuDto对象")
public class SysRoleMenuDto {
    @ApiModelProperty(value = "角色ID")
    @NotNull(message = "角色ID不能为空")
    private Long roleId;


    @ApiModelProperty(value = "菜单组")
    private Long[] menuIds;
}
