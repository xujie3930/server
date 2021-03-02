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

import java.util.Date;


/**
 * <p>
 *
 * </p>
 *
 * @author ziling
 * @since 2020-08-24
 */
@TableName("bas_template")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@SuppressWarnings("serial")
@Data
@ApiModel(value = "BasTemplate对象", description = "")
public class BasTemplate  {

    private static final long serialVersionUID = 1L;

    @Excel(name = "主键id")
    @TableId(value = "ID", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "模板类型")
    @Excel(name = "模板类型")
    private String templateType;

    @ApiModelProperty(value = "模板内容")
    @Excel(name = "模板内容")
    private String templateContent;

    @ApiModelProperty(value = "创建人id")
    @Excel(name = "创建人id")
    private String createId;

    @ApiModelProperty(value = "创建人")
    @Excel(name = "创建人")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    @Excel(name = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改人id")
    @Excel(name = "修改人id")
    private String updateId;

    @ApiModelProperty(value = "修改人")
    @Excel(name = "修改人")
    private String updateBy;

    @ApiModelProperty(value = "修改时间")
    @Excel(name = "修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "模板类型code")
    @Excel(name = "模板类型code")
    private String templateTypeCode;

}
