package com.szmsd.chargerules.domain;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.*;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.language.annotation.FieldJsonI18n;
import com.szmsd.common.core.language.constant.RedisLanguageTable;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "仓储业务操作详情", description = "仓储业务操作详情表")
@TableName("cha_warehouse_operation_details")
public class WarehouseOperationDetails extends BaseEntity {

    @ExcelIgnore
    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(exist = false)
    @ApiModelProperty(value = "ID", hidden = true)
    @ExcelProperty(value = "业务费用顺序", index = 0)
    private Integer rowId;

    @ApiModelProperty(value = "计费天数")
    @ExcelProperty(value = "计费天数", index = 1)
    private String chargeDays;

    @ApiModelProperty(value = "价格")
    @ExcelProperty(value = "价格", index = 2)
    private BigDecimal price;

    @ApiModelProperty(value = "计费单位")
    @ExcelProperty(value = "计费单位", index = 3)
    private String unit;

    @ExcelIgnore
    @ApiModelProperty(value = "仓租计价id")
    private Integer warehouseOperationId;

    @ExcelIgnore
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ExcelIgnore
    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "修改人")
    private String updateBy;

    @ApiModelProperty(value = "折扣率")
    @Excel(name = "折扣率")
    @ExcelProperty(value = "折扣率", index = 4)
    private BigDecimal discountRate;

}
