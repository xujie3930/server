package com.szmsd.bas.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * <p>
 * 短信发送表
 * </p>
 *
 * @author ziling
 * @since 2020-09-10
 */
@TableName("bas_vercode")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@SuppressWarnings("serial")
@Data
@ApiModel(value = "BasVercode对象", description = "短信发送表")
public class BasVercode {

    private static final long serialVersionUID = 1L;

    @Excel(name = "主键id")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "手机号")
    @Excel(name = "手机号")
    private String phone;

    @ApiModelProperty(value = "验证码")
    @Excel(name = "验证码")
    private String mesCode;

    @ApiModelProperty(value = "短信类型")
    @Excel(name = "短信类型")
    private String mseType;

    @ApiModelProperty(value = "创建人id")
    @Excel(name = "创建人id")
    private String createId;

    @ApiModelProperty(value = "创建人id")
    @Excel(name = "创建人id")
    private String createBy;

    @ApiModelProperty(value = "创建人id")
    @Excel(name = "创建人id")
    private String createTime;

    @ApiModelProperty(value = "短信内容")
    @Excel(name = "短信内容")
    private String message;

    @ApiModelProperty(value = "发送网点code")
    @Excel(name = "发送网点code")
    private String belongSiteCode;

    @ApiModelProperty(value = "发送网点 name")
    @Excel(name = "发送网点 name")
    private String belongSiteName;
}
