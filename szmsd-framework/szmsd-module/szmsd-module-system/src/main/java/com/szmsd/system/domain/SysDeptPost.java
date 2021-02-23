package com.szmsd.system.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.szmsd.common.core.annotation.Excel;

import javax.validation.constraints.NotBlank;


/**
* <p>
    * 机构部门与岗位关联表
    * </p>
*
* @author lzw
* @since 2020-07-06
*/
@Data
@Accessors(chain = true)
@ApiModel(value="机构部门与岗位关联表", description="SysDeptPost对象")
@TableName(value = "sys_dept_post")
public class SysDeptPost  {

//    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "部门编号")
    @NotBlank(message = "部门编号不能为空")
    @Excel(name = "部门编号")
    private String deptCode;

    @ApiModelProperty(value = "岗位编号")
    @Excel(name = "岗位编号")
    @NotBlank(message = "岗位编号不能为空")
    private String postCode;

    @ApiModelProperty(value = "岗位名称")
    @Excel(name = "岗位名称")
    private String postName;


}
