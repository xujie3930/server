package com.szmsd.bas.api.domain;

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
 * 地区信息
 * </p>
 *
 * @author gen
 * @since 2020-11-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value="地区信息", description="BasRegion对象")
public class BasRegion extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "ID")
    private Integer id;

    @ApiModelProperty(value = "父节点id")
    @Excel(name = "父节点id")
    private Integer pId;

    @ApiModelProperty(value = "地址类别:1国家 2省份 3市 4区 5街道")
    @Excel(name = "地址类别:1国家 2省份 3市 4区 5街道")
    private Integer type;

    @ApiModelProperty(value = "name")
    @Excel(name = "name")
    private String name;

    @ApiModelProperty(value = "地址简码")
    @Excel(name = "地址简码")
    private String addressCode;

    @ApiModelProperty(value = "英文名")
    @Excel(name = "英文名")
    private String enName;

    @ApiModelProperty(value = "创建ID",hidden = true)
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @ApiModelProperty(value = "修改者ID",hidden = true)
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;
}
