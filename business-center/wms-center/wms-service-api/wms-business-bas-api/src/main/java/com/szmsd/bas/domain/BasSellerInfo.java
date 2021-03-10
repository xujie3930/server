package com.szmsd.bas.domain;

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
@ApiModel(value="", description="BasSellerInfo对象")
public class BasSellerInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
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
    private String delFlag;

    @ApiModelProperty(value = "客户代码")
    @Excel(name = "客户代码")
    private Integer cusNo;

    @ApiModelProperty(value = "姓名")
    @Excel(name = "姓名")
    private String name;

    @ApiModelProperty(value = "邮箱")
    @Excel(name = "邮箱")
    private String email;

    @ApiModelProperty(value = "住址")
    @Excel(name = "住址")
    private String address;

    @ApiModelProperty(value = "国家")
    @Excel(name = "国家")
    private String country;

    @ApiModelProperty(value = "联系电话")
    @Excel(name = "联系电话")
    private String telephone;

    @ApiModelProperty(value = "身份证号码")
    @Excel(name = "身份证号码")
    private String idCard;

    @ApiModelProperty(value = "出口易账号")
    @Excel(name = "出口易账号")
    private String ck1Account;

    @ApiModelProperty(value = "业务经理")
    @Excel(name = "业务经理")
    private String serviceManager;

    @ApiModelProperty(value = "用户名")
    @Excel(name = "用户名")
    private String account;

    @ApiModelProperty(value = "初始注册邮箱")
    @Excel(name = "初始注册邮箱")
    private String initEmail;

    @ApiModelProperty(value = "客服")
    @Excel(name = "客服")
    private String serviceStaff;


}
