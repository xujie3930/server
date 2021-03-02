package com.szmsd.bas.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
* <p>
    * 重量区间设置
    * </p>
*
* @author 2
* @since 2021-01-11
*/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value="重量区间设置", description="BasWeightSection对象")
public class BasWeightSection extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @Excel(name = "id")
    @TableId(type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "重量段开始")
    @Excel(name = "重量段开始")
    private String weightStart;

    @ApiModelProperty(value = "重量段结束")
    @Excel(name = "重量段结束")
    private String weightEnd;

    @ApiModelProperty(value = "用户编码")
    @Excel(name = "用户编码")
    private String userCode;

    @ApiModelProperty(value = "用户名称")
    @Excel(name = "用户名称")
    private String userName;

    @ApiModelProperty(value = "创建人")
    @Excel(name = "创建人")
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @ApiModelProperty(value = "最后修改人")
    @Excel(name = "最后修改人")
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;

    @ApiModelProperty(value = "预留字段")
    @Excel(name = "预留字段")
    private String parm1;

    @ApiModelProperty(value = "预留字段")
    @Excel(name = "预留字段")
    private String parm2;

    @ApiModelProperty(value = "预留字段")
    @Excel(name = "预留字段")
    private String parm3;

    @ApiModelProperty(value = "预留字段")
    @Excel(name = "预留字段")
    private String parm4;

    @ApiModelProperty(value = "预留字段")
    @Excel(name = "预留字段")
    private String parm5;


}
