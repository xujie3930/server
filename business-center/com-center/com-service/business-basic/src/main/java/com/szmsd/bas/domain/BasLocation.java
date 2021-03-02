package com.szmsd.bas.domain;

import com.baomidou.mybatisplus.annotation.*;
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
 * @since 2020-09-18
 */
@TableName("bas_location")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@SuppressWarnings("serial")
@Data
@ApiModel(value = "BasLocation对象", description = "")
public class BasLocation {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @Excel(name = "主键id")
    @TableId(value = "ID", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "库位code")
    @Excel(name = "库位code")
    private String warehouseCode;

    @ApiModelProperty(value = "库位name")
    @Excel(name = "库位name")
    private String warehouseName;

    @ApiModelProperty(value = "库位类型")
    @Excel(name = "库位类型")
    private String warehouseType;

    @ApiModelProperty(value = "所属库区code")
    @Excel(name = "所属库区code")
    private String belongReservoirCode;

    @ApiModelProperty(value = "所属库区name")
    @Excel(name = "所属库区name")
    private String belongReservoirName;

    @ApiModelProperty(value = "库位规格")
    @Excel(name = "库位规格")
    private String warehouseSpec;

    @ApiModelProperty(value = "启用标识（0=启用 1=未启用）")
    @Excel(name = "启用标识（0=启用 1=未启用）")
    private String enableIden;

    @ApiModelProperty(value = "创建人id")
    @Excel(name = "创建人id")
    private String createId;

    @ApiModelProperty(value = "创建人")
    @Excel(name = "创建人")
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @ApiModelProperty(value = "创建人时间")
    @Excel(name = "创建人时间")
    private Date createTime;

    @ApiModelProperty(value = "修改人id")
    @Excel(name = "修改人id")
    private String updateId;

    @ApiModelProperty(value = "修改人")
    @Excel(name = "修改人")
    private String updateBy;

    @ApiModelProperty(value = "修改人时间")
    @Excel(name = "修改人时间")
    private Date updateTime;
}
