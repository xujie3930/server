package com.szmsd.system.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * <p>
 * 角色和部门关联表
 * </p>
 *
 * @author lzw
 * @since 2020-07-09
 */
@Data
@Accessors(chain = true)
@ApiModel(value="角色和部门关联表", description="SysRoleDept对象")
@TableName("sys_role_dept")
public class SysRoleDept  {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "角色ID")
    @Excel(name = "角色ID")
    private Long roleId;

    @ApiModelProperty(value = "部门ID")
    @Excel(name = "部门ID")
    private String deptCode;

    @ApiModelProperty(value = "岗位编号")
    @Excel(name = "岗位编号")
    private String postCode;

    @ApiModelProperty(value = "网点级别：数据字典维护")
    @Excel(name = "网点级别：数据字典维护")
    private String siteRank;
}
