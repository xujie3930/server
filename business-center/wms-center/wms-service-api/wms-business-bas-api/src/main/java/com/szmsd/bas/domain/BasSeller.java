package com.szmsd.bas.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.szmsd.common.core.annotation.Excel;


/**
* <p>
    * 
    * </p>
*
* @author l
* @since 2021-03-09
*/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value="", description="BasSeller对象")
public class BasSeller extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "id")
    private Integer id;

    @ApiModelProperty(value = "创建人")
    @Excel(name = "创建人")
    private String createBy;

    @ApiModelProperty(value = "修改人")
    @Excel(name = "修改人")
    private String updateBy;

    @ApiModelProperty(value = "删除标识：0未删除 1已删除")
    @Excel(name = "删除标识：0未删除 1已删除")
    @TableField(value = "del_flag", fill = FieldFill.INSERT)
    @TableLogic(value = "0", delval = "1")
    private String delFlag;

    @ApiModelProperty(value = "用户名")
    @Excel(name = "用户名")
    private String account;

    @ApiModelProperty(value = "初始注册邮箱")
    @Excel(name = "初始注册邮箱")
    private String initEmail;

    @ApiModelProperty(value = "客户代码")
    @Excel(name = "客户代码")
    private String cusNo;

    @ApiModelProperty(value = "密码")
    @Excel(name = "密码")
    private String password;

    @ApiModelProperty(value = "认证状态 ")
    @Excel(name = "认证状态 ")
    private Boolean state;

    @ApiModelProperty(value = "用户状态 生效 失效")
    @Excel(name = "用户状态 生效 失效")
    private Boolean isActive;

    @ApiModelProperty(value = "密码盐")
    @Excel(name = "密码盐")
    private String salt;

    @ApiModelProperty(value = "业务经理")
    @Excel(name = "业务经理")
    private String serviceManager;


}
