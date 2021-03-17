package com.msd.chargerules.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "仓储业务操作", description = "仓储业务操作表")
@TableName("cha_warehouse_operation")
public class WarehouseOperation extends BaseEntity {

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "仓库")
    @TableField
    private String warehouseCode;

    @ApiModelProperty(value = "计费天数")
    @TableField
    private Integer chargeDays;

    @ApiModelProperty(value = "价格")
    @TableField
    private BigDecimal price;

    @ApiModelProperty(value = "计费单位")
    @TableField
    private String unit;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人")
    private String createBy;

    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "修改人")
    private String updateBy;

}
