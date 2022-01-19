package com.szmsd.bas.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.szmsd.common.core.utils.StringToolkit;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.szmsd.common.core.annotation.Excel;

import java.util.List;
import java.util.Optional;


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
@ApiModel(value = "", description = "BasSeller对象")
public class BasSeller extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "id")
    private Long id;

    @ApiModelProperty(value = "创建人")
    @Excel(name = "创建人")
    private String createBy;

    @ApiModelProperty(value = "修改人")
    @Excel(name = "修改人")
    private String updateBy;

    @ApiModelProperty(value = "删除标识：0未删除 1已删除")
    @Excel(name = "删除标识：0未删除 1已删除")
    private String delFlag;

    @ApiModelProperty(value = "用户名")
    @Excel(name = "用户名")
    private String userName;

    @ApiModelProperty(value = "初始注册邮箱")
    @Excel(name = "初始注册邮箱")
    private String initEmail;

    @ApiModelProperty(value = "客户代码")
    @Excel(name = "客户代码")
    private String sellerCode;

    public void setSellerCode(String sellerCode) {
        this.sellerCode = sellerCode;
        Optional.ofNullable(sellerCode).ifPresent(x -> this.sellerCodeList = StringToolkit.getCodeByArray(sellerCode));
    }

    @TableField(exist = false)
    @ApiModelProperty(value = "客户代码")
    @Excel(name = "客户代码")
    private List<String> sellerCodeList;

    @ApiModelProperty(value = "认证状态 ")
    @Excel(name = "认证状态 ")
    private Boolean state;

    @ApiModelProperty(value = "用户状态 生效 失效")
    @Excel(name = "用户状态 生效 失效")
    private Boolean isActive;

    @ApiModelProperty(value = "业务经理-id")
    @Excel(name = "业务经理")
    private String serviceManager;

    @ApiModelProperty(value = "业务经理-编码")
    @Excel(name = "业务经理")
    private String serviceManagerName;

    @ApiModelProperty(value = "业务经理-名称")
    @Excel(name = "业务经理")
    private String serviceManagerNickName;

    @ApiModelProperty(value = "国家")
    @Excel(name = "国家")
    private String countryName;

    @ApiModelProperty(value = "国家")
    @Excel(name = "国家")
    private String countryCode;

    @ApiModelProperty(value = "客服-id")
    @Excel(name = "客服")
    private String serviceStaff;

    @ApiModelProperty(value = "客服姓名-编码")
    @Excel(name = "客服姓名")
    private String serviceStaffName;

    @ApiModelProperty(value = "客服姓名")
    @Excel(name = "客服姓名")
    private String serviceStaffNickName;

    @ApiModelProperty(value = "姓名")
    @Excel(name = "姓名")
    private String nickName;

    @ApiModelProperty(value = "邮箱")
    @Excel(name = "邮箱")
    private String email;

    @ApiModelProperty(value = "住址")
    @Excel(name = "住址")
    private String address;

    @ApiModelProperty(value = "联系电话")
    @Excel(name = "联系电话")
    private String phoneNumber;

    @ApiModelProperty(value = "身份证号码")
    @Excel(name = "身份证号码")
    private String idCard;

    @ApiModelProperty(value = "出口易账号")
    @Excel(name = "出口易账号")
    private String ck1Account;

    @ApiModelProperty(value = "中文名")
    @Excel(name = "中文名")
    private String nameCn;

    @ApiModelProperty(value = "英文名")
    @Excel(name = "英文名")
    private String nameEn;

    @ApiModelProperty(value = "公司")
    @Excel(name = "公司")
    private String company;

    @ApiModelProperty(value = "性别 F 女 M 男")
    @Excel(name = "性别 F 女 M 男")
    private String sex;

    @ApiModelProperty(value = "验货要求")
    @Excel(name = "验货要求")
    private String inspectionRequirement;

    @ApiModelProperty(value = "真实姓名")
    @Excel(name = "真实姓名")
    private String realName;

    @ApiModelProperty(value = "证件号码")
    @Excel(name = "证件号码")
    private String identificationNumber;

    @ApiModelProperty(value = "用户类型")
    @Excel(name = "用户类型")
    private String type;

    @ApiModelProperty(value = "实名状态")
    @Excel(name = "实名状态")
    private String realState;

    @ApiModelProperty(value = "折扣用户类型-编码", notes = "主子类别")
    @Excel(name = "折扣用户类型-编码")
    private String discountUserType;

    /**----------------CK1 INFO----------------*/
    /**
     * 把push set到 thirdPartSystemInfo中
     */
    @ApiModelProperty(value = "是否需要推送CK1")
    private Boolean pushFlag;

    @ApiModelProperty(value = "授权码")
    private String authorizationCode;

}
