package com.szmsd.chargerules.domain;

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
@ApiModel(value = "业务操作", description = "业务操作表")
@TableName("cha_operation")
public class Operation extends BaseEntity {

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "操作类型")
    @TableField
    private String operationType;

    @ApiModelProperty(value = "是否多SKU")
    @TableField
    private boolean manySku;

    @ApiModelProperty(value = "仓库")
    @TableField
    private String warehouseCode;

    @ApiModelProperty(value = "订单类型")
    @TableField
    private String orderType;

    @ApiModelProperty(value = "首件价格")
    @TableField
    private BigDecimal firstPrice;

    @ApiModelProperty(value = "续件价格")
    @TableField
    private BigDecimal nextPrice;

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
