package com.szmsd.chargerules.domain;

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

import java.time.LocalDateTime;


@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "仓储业务操作", description = "仓储业务操作表")
@TableName("cha_warehouse_operation")
public class WarehouseOperation extends BaseEntity {

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "仓库")
    @TableField
    private String warehouseCode;

    @TableField(exist = false)
    @FieldJsonI18n(type = RedisLanguageTable.BAS_WAREHOUSE)
    @ApiModelProperty(value = "仓库名称")
    private String warehouseName;

    @ApiModelProperty(value = "币种编码")
    @TableField
    private String currencyCode;

    @ApiModelProperty(value = "币种名称")
    @TableField
    private String currencyName;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人")
    private String createBy;

    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "修改人")
    private String updateBy;

    @ApiModelProperty(value = "客户类型编码")
    @Excel(name = "客户类型编码")
    private String cusTypeCode;

    @ApiModelProperty(value = "客户名称 A,B")
    @Excel(name = "客户名称 A,B")
    private String cusNameList;

    @ApiModelProperty(value = "客户编码 CNI1,CNI2")
    @Excel(name = "客户编码 CNI1,CNI2")
    private String cusCodeList;

    @ApiModelProperty(value = "生效时间")
    @Excel(name = "生效时间")
    private LocalDateTime effectiveTime;

    @ApiModelProperty(value = "失效时间")
    @Excel(name = "失效时间")
    private LocalDateTime expirationTime;

    public String getWarehouseName() {
        return warehouseCode;
    }

}